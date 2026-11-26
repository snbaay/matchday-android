package com.example.matchday.data.extension

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val CLUB_PREFERENCES_NAME = "saved_club_preferences"

val Context.clubPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = CLUB_PREFERENCES_NAME
)
