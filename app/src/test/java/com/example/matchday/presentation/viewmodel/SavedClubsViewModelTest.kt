package com.example.matchday.presentation.viewmodel

import com.example.matchday.data.local.FakeSavedClubDao
import com.example.matchday.data.repository.LocalSavedClubsRepository
import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.ClubSortOrder
import com.example.matchday.domain.model.SavedClub
import com.example.matchday.domain.repository.ClubSortPreferences
import com.example.matchday.domain.usecase.DeleteSavedClubUseCase
import com.example.matchday.domain.usecase.ObserveClubSortOrderUseCase
import com.example.matchday.domain.usecase.ObserveSavedClubsUseCase
import com.example.matchday.domain.usecase.SaveClubUseCase
import com.example.matchday.domain.usecase.SetClubSortOrderUseCase
import com.example.matchday.domain.usecase.UpdateClubNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SavedClubsViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun combinesSavedClubsAndSortOrderAndUpdatesNotesAndDeletion() = runTest {
        val repository = LocalSavedClubsRepository(FakeSavedClubDao())
        val sortFlow = MutableStateFlow(ClubSortOrder.ASCENDING)
        val preferences = object : ClubSortPreferences {
            override val sortOrder = sortFlow
            override suspend fun setSortOrder(order: ClubSortOrder) {
                sortFlow.value = order
            }
        }
        val viewModel = SavedClubsViewModel(
            ObserveSavedClubsUseCase(repository),
            ObserveClubSortOrderUseCase(preferences),
            SaveClubUseCase(repository),
            UpdateClubNoteUseCase(repository),
            DeleteSavedClubUseCase(repository),
            SetClubSortOrderUseCase(preferences)
        )

        val collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        runCurrent()

        val arsenal = Club("10", "Arsenal", null)
        val kairat = Club("701", "FK Kairat", "https://example.com/kairat.png")
        viewModel.save(kairat)
        assertEquals(SavedClubsMessage.ClubSaved("FK Kairat"), viewModel.messages.first())
        viewModel.save(arsenal)
        runCurrent()

        assertEquals(
            listOf("Arsenal", "FK Kairat"),
            viewModel.uiState.filterIsInstance<SavedClubsUiState.Content>().first().clubs.map(SavedClub::name)
        )

        viewModel.changeSortOrder(ClubSortOrder.DESCENDING)
        viewModel.updateNote("701", "Champions League")
        runCurrent()

        val sorted = viewModel.uiState.filterIsInstance<SavedClubsUiState.Content>().first()
        assertEquals(ClubSortOrder.DESCENDING, sorted.sortOrder)
        assertEquals(
            listOf(
                SavedClub("701", "FK Kairat", "https://example.com/kairat.png", "Champions League"),
                SavedClub("10", "Arsenal", null, "")
            ),
            sorted.clubs
        )

        viewModel.delete("10")
        runCurrent()
        assertEquals(
            listOf(SavedClub("701", "FK Kairat", "https://example.com/kairat.png", "Champions League")),
            viewModel.uiState.filterIsInstance<SavedClubsUiState.Content>().first().clubs
        )
        collector.cancel()
    }
}
