package com.example.matchday.domain.repository

import com.example.matchday.domain.model.AppData
import kotlinx.coroutines.flow.StateFlow

interface TournamentRepository {
    val state: StateFlow<AppData>

    fun createTournament(name: String): Result<String>
    fun addTeam(tournamentId: String, name: String): Result<String>
    fun createMatch(tournamentId: String, homeTeamId: String, awayTeamId: String): Result<String>
    fun saveScore(matchId: String, homeScore: Int, awayScore: Int): Result<Unit>
}
