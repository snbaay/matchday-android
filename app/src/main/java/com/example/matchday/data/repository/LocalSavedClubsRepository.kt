package com.example.matchday.data.repository

import com.example.matchday.data.local.SavedClubDao
import com.example.matchday.data.local.SavedClubEntity
import com.example.matchday.data.mapper.toDomain
import com.example.matchday.data.mapper.toEntity
import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.SavedClub
import com.example.matchday.domain.repository.SavedClubsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalSavedClubsRepository(
    private val dao: SavedClubDao
) : SavedClubsRepository {
    override fun observeSavedClubs(): Flow<List<SavedClub>> =
        dao.observeAll().map { entities -> entities.map(SavedClubEntity::toDomain) }

    override suspend fun saveClub(club: Club, note: String) {
        val entity = club.toEntity(note)
        if (entity.id.isEmpty() || entity.name.isEmpty()) return
        dao.insert(entity)
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
