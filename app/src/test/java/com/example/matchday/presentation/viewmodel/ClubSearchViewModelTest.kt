package com.example.matchday.presentation.viewmodel

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.ClubSearchException
import com.example.matchday.domain.repository.ClubSearchRepository
import com.example.matchday.domain.usecase.SearchClubsUseCase
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClubSearchViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun blankSearchDoesNothingAndSuccessfulSearchKeepsRepositoryOrder() = runTest {
        val clubs = listOf(Club("2", "Aston Villa", null), Club("1", "Arsenal", null))
        val queries = mutableListOf<String>()
        val viewModel = createViewModel { query ->
            queries += query
            clubs
        }

        viewModel.updateQuery("  ")
        viewModel.search()
        viewModel.retry()
        runCurrent()
        assertEquals(ClubSearchResult.Idle, viewModel.uiState.value.result)
        assertEquals(emptyList<String>(), queries)

        viewModel.updateQuery("  Aston  ")
        viewModel.search()
        runCurrent()

        assertEquals(listOf("Aston"), queries)
        assertEquals(
            ClubSearchUiState("  Aston  ", ClubSearchResult.Success(clubs)),
            viewModel.uiState.value
        )
    }

    @Test
    fun pendingRequestBlocksAnotherSearchAndCanFinishWithNoMatches() = runTest {
        val response = CompletableDeferred<List<Club>>()
        val queries = mutableListOf<String>()
        val viewModel = createViewModel { query ->
            queries += query
            response.await()
        }

        viewModel.updateQuery("missing")
        viewModel.search()
        assertEquals(ClubSearchResult.Loading, viewModel.uiState.value.result)
        viewModel.updateQuery("another club")
        viewModel.search()
        viewModel.retry()
        runCurrent()
        assertEquals(listOf("missing"), queries)

        response.complete(emptyList())
        runCurrent()
        assertEquals(ClubSearchUiState("another club", ClubSearchResult.Empty), viewModel.uiState.value)
    }

    @Test
    fun retryRepeatsFailedQueryEvenAfterInputWasEdited() = runTest {
        val club = Club("1", "FK Kairat", null)
        val queries = mutableListOf<String>()
        val viewModel = createViewModel { query ->
            queries += query
            if (queries.size == 1) throw ClubSearchException.Network(IOException("Offline"))
            listOf(club)
        }

        viewModel.updateQuery("Kairat")
        viewModel.search()
        runCurrent()
        assertEquals(ClubSearchResult.Error("Kairat"), viewModel.uiState.value.result)

        viewModel.updateQuery("Chelsea")
        viewModel.retry()
        runCurrent()

        assertEquals(listOf("Kairat", "Kairat"), queries)
        assertEquals(
            ClubSearchUiState("Chelsea", ClubSearchResult.Success(listOf(club))),
            viewModel.uiState.value
        )
    }

    private fun createViewModel(respond: suspend (String) -> List<Club>): ClubSearchViewModel {
        val repository = object : ClubSearchRepository {
            override suspend fun search(query: String): List<Club> = respond(query)
        }
        return ClubSearchViewModel(SearchClubsUseCase(repository))
    }
}
