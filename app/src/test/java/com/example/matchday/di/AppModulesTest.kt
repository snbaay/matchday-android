package com.example.matchday.di

import com.example.matchday.data.local.FakeSavedClubDao
import com.example.matchday.data.local.SavedClubDao
import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.SavedClub
import com.example.matchday.domain.repository.SavedClubsRepository
import com.example.matchday.domain.repository.TournamentRepository
import com.example.matchday.domain.usecase.SearchClubsUseCase
import com.example.matchday.presentation.viewmodel.AppViewModel
import com.example.matchday.presentation.viewmodel.ClubSearchUiState
import com.example.matchday.presentation.viewmodel.ClubSearchViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module

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

    @Test
    fun resolvesClubSearchDependencies() = runBlocking {
        val app = koinApplication { modules(dataModule, domainModule, presentationModule) }
        try {
            val search = app.koin.get<SearchClubsUseCase>()
            assertEquals(emptyList<Club>(), search(""))
            assertEquals(ClubSearchUiState(), app.koin.get<ClubSearchViewModel>().uiState.value)
        } finally {
            app.close()
        }
    }

    @Test
    fun resolvesSavedClubsDependencies() = runBlocking {
        val testDaoModule = module {
            single<SavedClubDao> { FakeSavedClubDao() }
        }
        val app = koinApplication {
            allowOverride(true)
            modules(dataModule, domainModule, presentationModule, testDaoModule)
        }
        try {
            val repository = app.koin.get<SavedClubsRepository>()
            repository.saveClub(Club("1", "Real Madrid", null), note = "15x UCL")
            assertEquals(
                listOf(SavedClub("1", "Real Madrid", null, "15x UCL")),
                repository.observeSavedClubs().first()
            )
        } finally {
            app.close()
        }
    }
}
