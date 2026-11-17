package com.example.matchday.data

import com.example.matchday.model.AppData
import com.example.matchday.model.Match
import com.example.matchday.model.Team
import com.example.matchday.model.Tournament
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryTournamentRepository : TournamentRepository {
    private val mutableState = MutableStateFlow(AppData())
    override val state: StateFlow<AppData> = mutableState.asStateFlow()

    override fun createTournament(name: String): Result<String> {
        val trimmedName = name.trim()
        if (trimmedName.isBlank() || trimmedName.length > 60) {
            return Result.failure(
                IllegalArgumentException("Tournament name must contain 1 to 60 characters.")
            )
        }

        val tournament = Tournament(UUID.randomUUID().toString(), trimmedName)
        mutableState.update { current ->
            current.copy(tournaments = current.tournaments + tournament)
        }
        return Result.success(tournament.id)
    }

    override fun addTeam(tournamentId: String, name: String): Result<String> {
        val trimmedName = name.trim()
        if (trimmedName.isBlank() || trimmedName.length > 40) {
            return Result.failure(
                IllegalArgumentException("Team name must contain 1 to 40 characters.")
            )
        }

        val current = mutableState.value
        if (current.tournaments.none { it.id == tournamentId }) {
            return Result.failure(IllegalArgumentException("Tournament not found."))
        }
        if (current.teams.any {
                it.tournamentId == tournamentId && it.name.equals(trimmedName, ignoreCase = true)
            }) {
            return Result.failure(
                IllegalArgumentException("A team with this name already exists in this tournament.")
            )
        }

        val team = Team(UUID.randomUUID().toString(), tournamentId, trimmedName)
        mutableState.update { data ->
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
            return Result.failure(IllegalArgumentException("Tournament not found."))
        }
        if (homeTeamId == awayTeamId) {
            return Result.failure(IllegalArgumentException("Choose two different teams."))
        }
        val homeTeam = current.teams.find { it.id == homeTeamId }
        val awayTeam = current.teams.find { it.id == awayTeamId }
        if (homeTeam == null || awayTeam == null ||
            homeTeam.tournamentId != tournamentId || awayTeam.tournamentId != tournamentId
        ) {
            return Result.failure(
                IllegalArgumentException("Both teams must belong to this tournament.")
            )
        }
        val duplicate = current.matches.any {
            it.tournamentId == tournamentId &&
                ((it.homeTeamId == homeTeamId && it.awayTeamId == awayTeamId) ||
                    (it.homeTeamId == awayTeamId && it.awayTeamId == homeTeamId))
        }
        if (duplicate) {
            return Result.failure(IllegalArgumentException("This pair already has a match."))
        }

        val match = Match(UUID.randomUUID().toString(), tournamentId, homeTeamId, awayTeamId)
        mutableState.update { data ->
            data.copy(matches = data.matches + match)
        }
        return Result.success(match.id)
    }

    override fun saveScore(matchId: String, homeScore: Int, awayScore: Int): Result<Unit> {
        if (homeScore !in 0..99 || awayScore !in 0..99) {
            return Result.failure(IllegalArgumentException("Scores must be between 0 and 99."))
        }
        if (mutableState.value.matches.none { it.id == matchId }) {
            return Result.failure(IllegalArgumentException("Match not found."))
        }

        mutableState.update { data ->
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
