package com.example.matchday.data.repository

import com.example.matchday.data.local.InMemoryTournamentDataSource
import com.example.matchday.data.mapper.toDomain
import com.example.matchday.data.mapper.toDto
import com.example.matchday.domain.model.AppData
import com.example.matchday.domain.model.Match
import com.example.matchday.domain.model.Team
import com.example.matchday.domain.model.Tournament
import com.example.matchday.domain.model.TournamentError
import com.example.matchday.domain.model.TournamentFailure
import com.example.matchday.domain.repository.TournamentRepository
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryTournamentRepository(
    private val dataSource: InMemoryTournamentDataSource
) : TournamentRepository {
    private val mutableState = MutableStateFlow(dataSource.read().toDomain())
    override val state: StateFlow<AppData> = mutableState.asStateFlow()

    private fun updateState(transform: (AppData) -> AppData) {
        val updated = transform(dataSource.read().toDomain())
        dataSource.write(updated.toDto())
        mutableState.value = dataSource.read().toDomain()
    }

    override fun createTournament(name: String): Result<String> {
        val trimmedName = name.trim()
        if (trimmedName.isBlank() || trimmedName.length > 60) {
            return Result.failure(TournamentFailure(TournamentError.INVALID_TOURNAMENT_NAME))
        }

        val tournament = Tournament(UUID.randomUUID().toString(), trimmedName)
        updateState { current ->
            current.copy(tournaments = current.tournaments + tournament)
        }
        return Result.success(tournament.id)
    }

    override fun addTeam(tournamentId: String, name: String): Result<String> {
        val trimmedName = name.trim()
        if (trimmedName.isBlank() || trimmedName.length > 40) {
            return Result.failure(TournamentFailure(TournamentError.INVALID_TEAM_NAME))
        }

        val current = mutableState.value
        if (current.tournaments.none { it.id == tournamentId }) {
            return Result.failure(TournamentFailure(TournamentError.TOURNAMENT_NOT_FOUND))
        }
        if (current.teams.any {
                it.tournamentId == tournamentId && it.name.equals(trimmedName, ignoreCase = true)
            }) {
            return Result.failure(TournamentFailure(TournamentError.DUPLICATE_TEAM))
        }

        val team = Team(UUID.randomUUID().toString(), tournamentId, trimmedName)
        updateState { data ->
            data.copy(teams = data.teams + team)
        }
        return Result.success(team.id)
    }

    override fun createMatch(
        tournamentId: String,
        homeTeamId: String,
        awayTeamId: String
    ): Result<String> {
        val current = mutableState.value
        if (current.tournaments.none { it.id == tournamentId }) {
            return Result.failure(TournamentFailure(TournamentError.TOURNAMENT_NOT_FOUND))
        }
        if (homeTeamId == awayTeamId) {
            return Result.failure(TournamentFailure(TournamentError.SAME_TEAM))
        }
        val homeTeam = current.teams.find { it.id == homeTeamId }
        val awayTeam = current.teams.find { it.id == awayTeamId }
        if (homeTeam == null || awayTeam == null ||
            homeTeam.tournamentId != tournamentId || awayTeam.tournamentId != tournamentId
        ) {
            return Result.failure(TournamentFailure(TournamentError.WRONG_TOURNAMENT))
        }
        val duplicate = current.matches.any {
            it.tournamentId == tournamentId &&
                ((it.homeTeamId == homeTeamId && it.awayTeamId == awayTeamId) ||
                    (it.homeTeamId == awayTeamId && it.awayTeamId == homeTeamId))
        }
        if (duplicate) {
            return Result.failure(TournamentFailure(TournamentError.DUPLICATE_MATCH))
        }

        val match = Match(UUID.randomUUID().toString(), tournamentId, homeTeamId, awayTeamId)
        updateState { data ->
            data.copy(matches = data.matches + match)
        }
        return Result.success(match.id)
    }

    override fun saveScore(matchId: String, homeScore: Int, awayScore: Int): Result<Unit> {
        if (homeScore !in 0..99 || awayScore !in 0..99) {
            return Result.failure(TournamentFailure(TournamentError.INVALID_SCORE))
        }
        if (mutableState.value.matches.none { it.id == matchId }) {
            return Result.failure(TournamentFailure(TournamentError.MATCH_NOT_FOUND))
        }

        updateState { data ->
            data.copy(matches = data.matches.map { match ->
                if (match.id == matchId) {
                    match.copy(homeScore = homeScore, awayScore = awayScore)
                } else {
                    match
                }
            })
        }
        return Result.success(Unit)
    }
}
