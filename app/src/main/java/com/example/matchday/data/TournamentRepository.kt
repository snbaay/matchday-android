package com.example.matchday.data

import com.example.matchday.model.AppData
import kotlinx.coroutines.flow.StateFlow

interface TournamentRepository {
    val state: StateFlow<AppData>

    fun createTournament(name: String): Result<String>
    fun addTeam(tournamentId: String, name: String): Result<String>
}
