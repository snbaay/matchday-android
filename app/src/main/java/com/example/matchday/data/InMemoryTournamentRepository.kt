package com.example.matchday.data

import com.example.matchday.model.AppData
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
}
