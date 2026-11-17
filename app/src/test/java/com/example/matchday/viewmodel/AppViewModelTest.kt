package com.example.matchday.viewmodel

import com.example.matchday.data.InMemoryTournamentRepository
import com.example.matchday.model.AppData
import com.example.matchday.model.Match
import com.example.matchday.model.Team
import com.example.matchday.model.Tournament
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class AppViewModelTest {
    @Test
    fun stateIsTheRepositoryStateAndReflectsExternalUpdates() {
        val repository = InMemoryTournamentRepository()
        val viewModel = AppViewModel(repository)

        assertSame(repository.state, viewModel.state)
        assertEquals(AppData(), viewModel.state.value)

        val id = repository.createTournament("Cup").getOrThrow()

        assertEquals(listOf(Tournament(id, "Cup")), viewModel.state.value.tournaments)
    }

    @Test
    fun publicOperationsUpdateSharedRepositoryState() {
        val repository = InMemoryTournamentRepository()
        val viewModel = AppViewModel(repository)

        val tournamentId = viewModel.createTournament("  Cup  ").getOrThrow()
        val homeId = viewModel.addTeam(tournamentId, "  Arsenal  ").getOrThrow()
        val awayId = viewModel.addTeam(tournamentId, "Barcelona").getOrThrow()
        val matchId = viewModel.createMatch(tournamentId, homeId, awayId).getOrThrow()
        viewModel.saveScore(matchId, 3, 1).getOrThrow()

        assertEquals(
            AppData(
                tournaments = listOf(Tournament(tournamentId, "Cup")),
                teams = listOf(
                    Team(homeId, tournamentId, "Arsenal"),
                    Team(awayId, tournamentId, "Barcelona")
                ),
                matches = listOf(Match(matchId, tournamentId, homeId, awayId, 3, 1))
            ),
            viewModel.state.value
        )
        assertEquals(repository.state.value, viewModel.state.value)
    }

    @Test
    fun repositoryFailuresReachCallerWithoutChangingState() {
        val viewModel = AppViewModel()
        val tournamentId = viewModel.createTournament("Cup").getOrThrow()
        val teamId = viewModel.addTeam(tournamentId, "Arsenal").getOrThrow()
        val previousState = viewModel.state.value

        val results = listOf(
            viewModel.createTournament("  "),
            viewModel.addTeam(tournamentId, " arsenal "),
            viewModel.createMatch(tournamentId, teamId, teamId),
            viewModel.saveScore("missing", -1, 100)
        )

        results.forEach { result ->
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        }
        assertEquals(previousState, viewModel.state.value)
    }
}
