package com.example.matchday.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.SavedClub
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedClubDaoTest {
    private lateinit var database: SavedClubsDatabase
    private lateinit var repository: LocalSavedClubsRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, SavedClubsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = LocalSavedClubsRepository(database.savedClubDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun crudOperationsPersistAndEmitThroughRoom() = runBlocking {
        val kairat = Club(id = "701", name = "FK Kairat", badgeUrl = "https://example.com/kairat.png")
        val arsenal = Club(id = "10", name = "Arsenal", badgeUrl = null)

        repository.saveClub(kairat)
        repository.saveClub(arsenal)

        assertEquals(
            listOf(
                SavedClub("10", "Arsenal", null, ""),
                SavedClub("701", "FK Kairat", "https://example.com/kairat.png", "")
            ),
            repository.observeSavedClubs().first()
        )

        repository.updateNote("701", "Champions League")
        repository.saveClub(kairat)

        assertEquals(
            "Champions League",
            repository.observeSavedClubs().first().first { it.id == "701" }.note
        )

        repository.deleteClub("10")
        assertEquals(
            listOf(SavedClub("701", "FK Kairat", "https://example.com/kairat.png", "Champions League")),
            repository.observeSavedClubs().first()
        )
    }
}
