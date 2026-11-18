package com.example.matchday.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.matchday.domain.model.AppData
import com.example.matchday.domain.model.StandingRow
import com.example.matchday.domain.usecase.AddTeamUseCase
import com.example.matchday.domain.usecase.CreateMatchUseCase
import com.example.matchday.domain.usecase.CreateTournamentUseCase
import com.example.matchday.domain.usecase.GetStandingsUseCase
import com.example.matchday.domain.usecase.ObserveTournamentsUseCase
import com.example.matchday.domain.usecase.SaveScoreUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(
    observeTournaments: ObserveTournamentsUseCase,
    private val createTournamentUseCase: CreateTournamentUseCase,
    private val addTeamUseCase: AddTeamUseCase,
    private val createMatchUseCase: CreateMatchUseCase,
    private val saveScoreUseCase: SaveScoreUseCase,
    private val getStandingsUseCase: GetStandingsUseCase
) : ViewModel() {
    val state: StateFlow<AppData> = observeTournaments()
    private val mutableUiState = MutableStateFlow(AppUiState(state.value))
    val uiState: StateFlow<AppUiState> = mutableUiState.asStateFlow()

    private fun <T> refreshAfter(result: Result<T>): Result<T> {
        mutableUiState.value = AppUiState(state.value)
        return result
    }

    fun createTournament(name: String): Result<String> = refreshAfter(createTournamentUseCase(name))

    fun addTeam(tournamentId: String, name: String): Result<String> =
        refreshAfter(addTeamUseCase(tournamentId, name))

    fun createMatch(
        tournamentId: String,
        homeTeamId: String,
        awayTeamId: String
    ): Result<String> = refreshAfter(createMatchUseCase(tournamentId, homeTeamId, awayTeamId))

    fun saveScore(matchId: String, homeScore: Int, awayScore: Int): Result<Unit> =
        refreshAfter(saveScoreUseCase(matchId, homeScore, awayScore))

    fun standingsFor(tournamentId: String): List<StandingRow> {
        return getStandingsUseCase(tournamentId)
    }
}
