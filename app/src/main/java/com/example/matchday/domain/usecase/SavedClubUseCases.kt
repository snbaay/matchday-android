package com.example.matchday.domain.usecase

import com.example.matchday.domain.repository.SavedClubsRepository

class ObserveSavedClubsUseCase(private val repository: SavedClubsRepository) {
    operator fun invoke() = repository.observeSavedClubs()
}
