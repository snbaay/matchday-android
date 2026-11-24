package com.example.matchday.domain.repository

import com.example.matchday.domain.model.Club

interface ClubSearchRepository {
    suspend fun search(query: String): List<Club>
}
