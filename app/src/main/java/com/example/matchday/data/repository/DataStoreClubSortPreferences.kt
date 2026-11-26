package com.example.matchday.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.matchday.domain.model.ClubSortOrder
import com.example.matchday.domain.repository.ClubSortPreferences
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DataStoreClubSortPreferences(
    private val dataStore: DataStore<Preferences>
) : ClubSortPreferences {
    override val sortOrder = dataStore.data.map { preferences ->
        if (preferences[sortDescending] == true) ClubSortOrder.DESCENDING
        else ClubSortOrder.ASCENDING
    }.distinctUntilChanged()

    override suspend fun setSortOrder(order: ClubSortOrder) {
        dataStore.edit { it[sortDescending] = order == ClubSortOrder.DESCENDING }
    }

    private companion object {
        val sortDescending = booleanPreferencesKey("sort_descending")
    }
}
