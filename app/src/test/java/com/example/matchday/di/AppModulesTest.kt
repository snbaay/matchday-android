package com.example.matchday.di

import com.example.matchday.data.local.FakeSavedClubDao
import com.example.matchday.data.local.SavedClubDao
import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.ClubSortOrder
import com.example.matchday.domain.model.SavedClub
import com.example.matchday.domain.repository.ClubSortPreferences
import com.example.matchday.domain.repository.SavedClubsRepository
import com.example.matchday.domain.repository.TournamentRepository
import com.example.matchday.domain.usecase.SearchClubsUseCase
import com.example.matchday.presentation.viewmodel.AppViewModel
import com.example.matchday.presentation.viewmodel.ClubSearchUiState
import com.example.matchday.presentation.viewmodel.ClubSearchViewModel
import com.example.matchday.presentation.viewmodel.SavedClubsUiState
import com.example.matchday.presentation.viewmodel.SavedClubsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
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
        val sortFlow = MutableStateFlow(ClubSortOrder.ASCENDING)
        val testLocalModule = module {
            single<SavedClubDao> { FakeSavedClubDao() }
            single<ClubSortPreferences> {
                object : ClubSortPreferences {
                    override val sortOrder = sortFlow
                    override suspend fun setSortOrder(order: ClubSortOrder) {
                        sortFlow.value = order
                    }
                }
            }
        }
        val app = koinApplication {
            allowOverride(true)
            modules(dataModule, domainModule, presentationModule, testLocalModule)
        }
        try {
            val repository = app.koin.get<SavedClubsRepository>()
            val viewModel = app.koin.get<SavedClubsViewModel>()
            repository.saveClub(Club("1", "Real Madrid", null), note = "15x UCL")
            assertEquals(
                listOf(SavedClub("1", "Real Madrid", null, "15x UCL")),
                repository.observeSavedClubs().first()
            )
            assertEquals(
                SavedClubsUiState.Content(
                    listOf(SavedClub("1", "Real Madrid", null, "15x UCL")),
                    ClubSortOrder.ASCENDING
                ),
                viewModel.uiState.filterIsInstance<SavedClubsUiState.Content>().first()
            )
        } finally {
            app.close()
        }
    }
}
