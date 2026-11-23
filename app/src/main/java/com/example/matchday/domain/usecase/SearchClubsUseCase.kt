package com.example.matchday.domain.usecase

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.repository.ClubSearchRepository

class SearchClubsUseCase(private val repository: ClubSearchRepository) {
    suspend operator fun invoke(query: String): List<Club> = repository.search(query)
}
