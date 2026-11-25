package com.example.matchday.domain.usecase

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.ClubSortOrder
import com.example.matchday.domain.repository.ClubSortPreferences
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

class DeleteSavedClubUseCase(private val repository: SavedClubsRepository) {
    suspend operator fun invoke(clubId: String) = repository.deleteClub(clubId)
}

class ObserveClubSortOrderUseCase(private val preferences: ClubSortPreferences) {
    operator fun invoke() = preferences.sortOrder
}

class SetClubSortOrderUseCase(private val preferences: ClubSortPreferences) {
    suspend operator fun invoke(order: ClubSortOrder) = preferences.setSortOrder(order)
}
