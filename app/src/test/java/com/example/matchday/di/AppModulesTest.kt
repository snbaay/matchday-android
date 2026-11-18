package com.example.matchday.di

import com.example.matchday.domain.repository.TournamentRepository
import com.example.matchday.presentation.viewmodel.AppViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.dsl.koinApplication

class AppModulesTest {
    @Test
    fun modulesResolveViewModelAndSharedRepository() {
        val app = koinApplication { modules(dataModule, domainModule, presentationModule) }
        try {
            val viewModel = app.koin.get<AppViewModel>()
            val repository = app.koin.get<TournamentRepository>()
            val id = viewModel.createTournament("Cup").getOrThrow()
            assertEquals(id, repository.state.value.tournaments.single().id)
        } finally {
            app.close()
        }
    }
}
