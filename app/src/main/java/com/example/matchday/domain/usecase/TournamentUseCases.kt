package com.example.matchday.domain.usecase

import com.example.matchday.domain.model.AppData
import com.example.matchday.domain.model.StandingRow
import com.example.matchday.domain.repository.TournamentRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveTournamentsUseCase(private val repository: TournamentRepository) {
    operator fun invoke(): StateFlow<AppData> = repository.state
}

class CreateTournamentUseCase(private val repository: TournamentRepository) {
    operator fun invoke(name: String): Result<String> = repository.createTournament(name)
}

class AddTeamUseCase(private val repository: TournamentRepository) {
    operator fun invoke(tournamentId: String, name: String): Result<String> =
        repository.addTeam(tournamentId, name)
}

class CreateMatchUseCase(private val repository: TournamentRepository) {
    operator fun invoke(tournamentId: String, homeTeamId: String, awayTeamId: String): Result<String> =
        repository.createMatch(tournamentId, homeTeamId, awayTeamId)
}

class SaveScoreUseCase(private val repository: TournamentRepository) {
    operator fun invoke(matchId: String, homeScore: Int, awayScore: Int): Result<Unit> =
        repository.saveScore(matchId, homeScore, awayScore)
}

class GetStandingsUseCase(
    private val repository: TournamentRepository,
    private val calculateStandings: CalculateStandingsUseCase
) {
    operator fun invoke(tournamentId: String): List<StandingRow> {
        val data = repository.state.value
        return calculateStandings(
            data.teams.filter { it.tournamentId == tournamentId },
            data.matches.filter { it.tournamentId == tournamentId }
        )
    }
}
