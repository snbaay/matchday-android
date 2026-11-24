package com.example.matchday.domain.repository

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.SavedClub
import kotlinx.coroutines.flow.Flow

interface SavedClubsRepository {
    fun observeSavedClubs(): Flow<List<SavedClub>>
    suspend fun saveClub(club: Club, note: String = "")
    suspend fun updateNote(clubId: String, note: String)
    suspend fun deleteClub(clubId: String)
}
