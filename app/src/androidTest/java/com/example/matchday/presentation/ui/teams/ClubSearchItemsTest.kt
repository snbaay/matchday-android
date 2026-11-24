package com.example.matchday.presentation.ui.teams

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.ClubSearchException
import com.example.matchday.domain.repository.ClubSearchRepository
import com.example.matchday.domain.usecase.SearchClubsUseCase
import com.example.matchday.presentation.ui.theme.MatchdayTheme
import com.example.matchday.presentation.viewmodel.ClubSearchViewModel
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClubSearchItemsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun searchShowsLoadingErrorRetryAndEmptyResults() {
        val firstResponse = CompletableDeferred<List<Club>>()
        val queries = mutableListOf<String>()
        showTeams { query ->
            queries += query
            if (queries.size == 1) firstResponse.await() else emptyList()
        }

        composeRule.onNodeWithText("Search clubs").assertIsNotEnabled()
        composeRule.onNode(hasSetTextAction()).performTextInput("missing")
        composeRule.onNodeWithText("Search clubs").performClick()
        composeRule.onNodeWithText("Searching for clubs…").assertIsDisplayed()
        composeRule.onNodeWithText("Search clubs").assertIsNotEnabled()

        composeRule.runOnIdle {
            firstResponse.completeExceptionally(ClubSearchException.Network(IOException("Offline")))
        }
        composeRule.onNodeWithText("Club search failed for “missing”.").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        composeRule.onNodeWithText("No matching clubs found.").assertIsDisplayed()
        composeRule.runOnIdle { assertEquals(listOf("missing", "missing"), queries) }
    }

    @Test
    fun keyboardSearchShowsClubAndSavePassesSelectedObject() {
        val club = Club("1", "FK Kairat", null)
        val otherClub = Club("2", "Kairat Academy", null)
        var saved: Club? = null
        showTeams(onSaveClub = { saved = it }) { query ->
            if (query == "kairat") listOf(otherClub, club) else emptyList()
        }

        val query = composeRule.onNode(hasSetTextAction())
        query.performTextInput("kairat")
        query.performImeAction()

        composeRule.onNodeWithText(club.name).assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Save FK Kairat").performClick()
        composeRule.runOnIdle { assertSame(club, saved) }
    }

    private fun showTeams(
        onSaveClub: ((Club) -> Unit)? = null,
        respond: suspend (String) -> List<Club>
    ) {
        val repository = object : ClubSearchRepository {
            override suspend fun search(query: String): List<Club> = respond(query)
        }
        val viewModel = ClubSearchViewModel(SearchClubsUseCase(repository))
        composeRule.setContent {
            MatchdayTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                TeamsScreen(
                    tournamentName = "Cup",
                    teams = emptyList(),
                    onAddTeam = { Result.success("team-id") },
                    onBack = {},
                    clubSearch = {
                        clubSearchItems(
                            state = state,
                            onQueryChange = viewModel::updateQuery,
                            onSearch = viewModel::search,
                            onRetry = viewModel::retry,
                            onSaveClub = onSaveClub
                        )
                    }
                )
            }
        }
    }
}
