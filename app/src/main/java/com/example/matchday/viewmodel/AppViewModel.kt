package com.example.matchday.viewmodel

import androidx.lifecycle.ViewModel
import com.example.matchday.data.InMemoryTournamentRepository
import com.example.matchday.data.TournamentRepository
import com.example.matchday.model.AppData
import com.example.matchday.model.StandingRow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(
    private val repository: TournamentRepository = InMemoryTournamentRepository()
) : ViewModel() {
    val state: StateFlow<AppData> = repository.state

    fun createTournament(name: String): Result<String> = repository.createTournament(name)

    fun addTeam(tournamentId: String, name: String): Result<String> =
        repository.addTeam(tournamentId, name)

    fun createMatch(
        tournamentId: String,
        homeTeamId: String,
        awayTeamId: String
    ): Result<String> = repository.createMatch(tournamentId, homeTeamId, awayTeamId)

    fun saveScore(matchId: String, homeScore: Int, awayScore: Int): Result<Unit> =
        repository.saveScore(matchId, homeScore, awayScore)

    fun standingsFor(tournamentId: String): List<StandingRow> {
        // TODO P1-10: delegate to P2's StandingsCalculator after integration.
        return emptyList()
    }
}
