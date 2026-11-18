package com.example.matchday.presentation.viewmodel

import com.example.matchday.data.InMemoryTournamentRepository
import com.example.matchday.data.local.InMemoryTournamentDataSource
import com.example.matchday.domain.usecase.AddTeamUseCase
import com.example.matchday.domain.usecase.CreateMatchUseCase
import com.example.matchday.domain.usecase.CreateTournamentUseCase
import com.example.matchday.domain.usecase.GetStandingsUseCase
import com.example.matchday.domain.usecase.CalculateStandingsUseCase
import com.example.matchday.domain.usecase.ObserveTournamentsUseCase
import com.example.matchday.domain.usecase.SaveScoreUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppViewModelTest {
    @Test fun operationsFlowThroughUseCasesAndScoresRecalculate() {
        val repository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        val viewModel = AppViewModel(
            ObserveTournamentsUseCase(repository), CreateTournamentUseCase(repository),
            AddTeamUseCase(repository), CreateMatchUseCase(repository),
            SaveScoreUseCase(repository), GetStandingsUseCase(repository, CalculateStandingsUseCase())
        )
        val tournament = viewModel.createTournament(" Cup ").getOrThrow()
        val home = viewModel.addTeam(tournament, "Arsenal").getOrThrow()
        val away = viewModel.addTeam(tournament, "Barcelona").getOrThrow()
        val match = viewModel.createMatch(tournament, home, away).getOrThrow()
        assertEquals(1, viewModel.uiState.value.data.matches.size)
        viewModel.saveScore(match, 3, 1).getOrThrow()
        assertEquals(3, viewModel.standingsFor(tournament).first().points)
        viewModel.saveScore(match, 0, 0).getOrThrow()
        assertEquals(listOf(1, 1), viewModel.standingsFor(tournament).map { it.points })
        assertEquals(1, viewModel.state.value.matches.size)
        assertTrue(viewModel.addTeam(tournament, " arsenal ").isFailure)
    }
}
