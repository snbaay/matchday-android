package com.example.matchday.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.ClubSortOrder
import com.example.matchday.domain.model.SavedClub
import com.example.matchday.domain.usecase.DeleteSavedClubUseCase
import com.example.matchday.domain.usecase.ObserveClubSortOrderUseCase
import com.example.matchday.domain.usecase.ObserveSavedClubsUseCase
import com.example.matchday.domain.usecase.SaveClubUseCase
import com.example.matchday.domain.usecase.SetClubSortOrderUseCase
import com.example.matchday.domain.usecase.UpdateClubNoteUseCase
import com.example.matchday.presentation.viewmodel.SavedClubsUiState
import com.example.matchday.presentation.viewmodel.SavedClubsViewModel
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedClubsPersistenceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val databaseName = "saved-clubs-test-${UUID.randomUUID()}"
    private val preferencesFile = context.cacheDir.resolve("$databaseName.preferences_pb")

    @Test
    fun restoresTwoClubsAnEditedNoteAndTheChosenOrder() = runBlocking {
        val arsenal = Club(id = "10", name = "Arsenal", badgeUrl = null)
        val kairat = Club(id = "701", name = "FK Kairat", badgeUrl = "https://example.com/kairat.png")
        val savedArsenal = SavedClub(arsenal.id, arsenal.name)
        val savedKairat = SavedClub(kairat.id, kairat.name, kairat.badgeUrl)
        val editedKairat = savedKairat.copy(note = "Champions League")

        try {
            withViewModel { viewModel ->
                assertEquals(ClubSortOrder.ASCENDING, viewModel.awaitContent().sortOrder)

                withContext(Dispatchers.Main) {
                    viewModel.save(kairat)
                    viewModel.save(arsenal)
                }
                assertEquals(listOf(savedArsenal, savedKairat), viewModel.awaitContent { it.clubs.size == 2 }.clubs)

                withContext(Dispatchers.Main) {
                    viewModel.updateNote(kairat.id, editedKairat.note)
                    viewModel.changeSortOrder(ClubSortOrder.DESCENDING)
                }
                val updated = viewModel.awaitContent {
                    it.sortOrder == ClubSortOrder.DESCENDING && editedKairat in it.clubs
                }
                assertEquals(listOf(editedKairat, savedArsenal), updated.clubs)
            }

            withViewModel { viewModel ->
                assertEquals(
                    SavedClubsUiState.Content(listOf(editedKairat, savedArsenal), ClubSortOrder.DESCENDING),
                    viewModel.awaitContent()
                )
                withContext(Dispatchers.Main) { viewModel.delete(arsenal.id) }
                assertEquals(listOf(editedKairat), viewModel.awaitContent { it.clubs.size == 1 }.clubs)
            }
        } finally {
            context.deleteDatabase(databaseName)
            preferencesFile.delete()
        }
        Unit
    }

    private suspend fun withViewModel(block: suspend (SavedClubsViewModel) -> Unit) {
        val database = Room.databaseBuilder(context, SavedClubsDatabase::class.java, databaseName).build()
        val dataStoreJob = SupervisorJob()
        val store = ViewModelStore()
        try {
            val repository = LocalSavedClubsRepository(database.savedClubDao())
            val preferences = DataStoreClubSortPreferences(
                PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO + dataStoreJob)) {
                    preferencesFile
                }
            )
            val factory = viewModelFactory {
                initializer {
                    SavedClubsViewModel(
                        ObserveSavedClubsUseCase(repository),
                        ObserveClubSortOrderUseCase(preferences),
                        SaveClubUseCase(repository),
                        UpdateClubNoteUseCase(repository),
                        DeleteSavedClubUseCase(repository),
                        SetClubSortOrderUseCase(preferences)
                    )
                }
            }
            val viewModel = withContext(Dispatchers.Main) {
                ViewModelProvider.create(store, factory)[SavedClubsViewModel::class]
            }
            block(viewModel)
        } finally {
            withContext(Dispatchers.Main) { store.clear() }
            dataStoreJob.cancelAndJoin()
            database.close()
        }
    }

    private suspend fun SavedClubsViewModel.awaitContent(
        predicate: (SavedClubsUiState.Content) -> Boolean = { true }
    ): SavedClubsUiState.Content = withTimeout(10_000) {
        uiState.filterIsInstance<SavedClubsUiState.Content>().first(predicate)
    }
}
