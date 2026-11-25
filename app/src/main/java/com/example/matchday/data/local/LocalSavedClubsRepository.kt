package com.example.matchday.data.local

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.SavedClub
import com.example.matchday.domain.repository.SavedClubsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LocalSavedClubsRepository(
    private val dao: SavedClubDao
) : SavedClubsRepository {
    override fun observeSavedClubs(): Flow<List<SavedClub>> =
        dao.observeAll().map { entities -> entities.map(SavedClubEntity::toDomain) }

    override suspend fun saveClub(club: Club, note: String) {
        val id = club.id.trim()
        val name = club.name.trim()
        if (id.isEmpty() || name.isEmpty()) return

        dao.insert(
            SavedClubEntity(
                id = id,
                name = name,
                badgeUrl = club.badgeUrl?.trim()?.ifEmpty { null },
                note = note.trim()
            )
        )
    }

    override suspend fun updateNote(clubId: String, note: String) {
        val id = clubId.trim()
        if (id.isEmpty()) return
        dao.updateNote(id, note.trim())
    }

    override suspend fun deleteClub(clubId: String) {
        val id = clubId.trim()
        if (id.isEmpty()) return
        dao.deleteById(id)
    }
}

internal fun SavedClubEntity.toDomain(): SavedClub = SavedClub(
    id = id,
    name = name,
    badgeUrl = badgeUrl,
    note = note
)
