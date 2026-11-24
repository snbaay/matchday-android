package com.example.matchday.data.local

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.SavedClub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LocalSavedClubsRepositoryTest {
    private lateinit var fakeDao: FakeSavedClubDao
    private lateinit var repository: LocalSavedClubsRepository

    @Before
    fun setUp() {
        fakeDao = FakeSavedClubDao()
        repository = LocalSavedClubsRepository(fakeDao)
    }

    @Test
    fun savesReadsUpdatesAndDeletesClubsById() = runTest {
        val kairat = Club(id = " 701 ", name = " FK Kairat ", badgeUrl = " https://example.com/kairat.png ")
        val arsenal = Club(id = "10", name = "Arsenal", badgeUrl = "   ")

        assertEquals(emptyList<SavedClub>(), repository.observeSavedClubs().first())

        repository.saveClub(kairat)
        repository.saveClub(arsenal, note = " Premier League ")
        repository.saveClub(Club(id = "   ", name = "Invalid", badgeUrl = null))

        assertEquals(
            listOf(
                SavedClub("10", "Arsenal", null, "Premier League"),
                SavedClub("701", "FK Kairat", "https://example.com/kairat.png", "")
            ),
            repository.observeSavedClubs().first()
        )

        repository.updateNote("701", " UCL participant ")
        assertEquals(
            listOf(
                SavedClub("10", "Arsenal", null, "Premier League"),
                SavedClub("701", "FK Kairat", "https://example.com/kairat.png", "UCL participant")
            ),
            repository.observeSavedClubs().first()
        )

        repository.saveClub(kairat)
        assertEquals(
            "UCL participant",
            repository.observeSavedClubs().first().first { it.id == "701" }.note
        )

        repository.deleteClub("10")
        assertEquals(
            listOf(SavedClub("701", "FK Kairat", "https://example.com/kairat.png", "UCL participant")),
            repository.observeSavedClubs().first()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun emitsReactiveUpdatesOnInsertUpdateAndDelete() = runTest {
        val emissions = mutableListOf<List<SavedClub>>()
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observeSavedClubs().take(4).toList(emissions)
        }

        val bayern = Club(id = "40", name = "FC Bayern München", badgeUrl = null)
        repository.saveClub(bayern)
        repository.updateNote("40", "5x UCL")
        repository.deleteClub("40")

        collectJob.join()
        assertEquals(
            listOf(
                emptyList(),
                listOf(SavedClub("40", "FC Bayern München", null, "")),
                listOf(SavedClub("40", "FC Bayern München", null, "5x UCL")),
                emptyList()
            ),
            emissions
        )
    }
}

internal class FakeSavedClubDao : SavedClubDao {
    private val rowsById = MutableStateFlow<Map<String, SavedClubEntity>>(emptyMap())

    override suspend fun insert(club: SavedClubEntity) {
        rowsById.update { current ->
            if (club.id in current) current else current + (club.id to club)
        }
    }

    override fun observeAll(): Flow<List<SavedClubEntity>> =
        rowsById.map { current ->
            current.values.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER, SavedClubEntity::name)
                    .thenBy(SavedClubEntity::id)
            )
        }

    override suspend fun updateNote(clubId: String, note: String) {
        rowsById.update { current ->
            val existing = current[clubId] ?: return@update current
            current + (clubId to existing.copy(note = note))
        }
    }

    override suspend fun deleteById(clubId: String) {
        rowsById.update { current -> current - clubId }
    }
}
