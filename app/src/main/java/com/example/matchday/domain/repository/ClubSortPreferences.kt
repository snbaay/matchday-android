package com.example.matchday.domain.repository

import com.example.matchday.domain.model.ClubSortOrder
import kotlinx.coroutines.flow.Flow

interface ClubSortPreferences {
    val sortOrder: Flow<ClubSortOrder>
    suspend fun setSortOrder(order: ClubSortOrder)
}
