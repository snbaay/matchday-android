package com.example.matchday.domain.usecase

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.repository.SavedClubsRepository

class ObserveSavedClubsUseCase(private val repository: SavedClubsRepository) {
    operator fun invoke() = repository.observeSavedClubs()
}

class SaveClubUseCase(private val repository: SavedClubsRepository) {
    suspend operator fun invoke(club: Club) = repository.saveClub(club)
}

class UpdateClubNoteUseCase(private val repository: SavedClubsRepository) {
    suspend operator fun invoke(clubId: String, note: String) = repository.updateNote(clubId, note)
}
