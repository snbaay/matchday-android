package com.example.matchday.ui.teams

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.matchday.model.Team
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import com.example.matchday.ui.theme.MatchdayTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TeamsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun buttonsHaveClickActionsAndNameFieldIsLabelledAndEditable() {
        showValidationScreen(mutableListOf())
        composeRule.onNodeWithText("Back").assertIsEnabled().assertHasClickAction()
        composeRule.onNodeWithText("Add Team").assertIsEnabled()
            .assertHasClickAction().performClick()
        composeRule.onNodeWithText("Save").assertIsDisplayed().assertIsEnabled()
            .assertHasClickAction()
        composeRule.onNodeWithText("Cancel").assertIsDisplayed().assertIsEnabled()
            .assertHasClickAction()
        val field = composeRule.onNode(hasSetTextAction() and hasText("Team name"))
        field.performTextInput("Chelsea")
        field.assertTextEquals("Team name", "Chelsea")
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNode(isDialog()).assertDoesNotExist()
    }

    @Test
    fun longNameWrapsOnSmallScreenAndAddTeamRemainsAvailable() {
        val longName = "Westminster Wolverhampton Wanderers Club"
        composeRule.setContent {
            MatchdayTheme {
                Box(modifier = Modifier.requiredSize(320.dp, 568.dp)) {
                    TeamsScreen(
                        tournamentName = "International Student Football Championship",
                        teams = listOf(Team("long-id", "cup-id", longName)),
                        onAddTeam = { Result.success("team-id") },
                        onBack = {}
                    )
                }
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(longName).assertIsDisplayed()
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertTrue(layouts.isNotEmpty())
        assertTrue(layouts.first().lineCount > 1)
        assertTrue(!layouts.first().hasVisualOverflow)
        composeRule.onNodeWithText("Add Team").assertIsDisplayed().assertIsEnabled()
            .performClick()
        composeRule.onNode(isDialog()).assertIsDisplayed()
    }

    @Test
    fun emptyNameShowsErrorWithoutCallingCallback() {
        checkRejectedName("", "Enter team name")
    }

    @Test
    fun spacesOnlyNameShowsErrorWithoutCallingCallback() {
        checkRejectedName("   ", "Enter team name")
    }

    @Test
    fun nameIsTrimmedBeforeCallingCallback() {
        checkAcceptedName("  Arsenal  ", "Arsenal")
    }

    @Test
    fun fortyCharacterNameIsAccepted() {
        checkAcceptedName("A".repeat(40), "A".repeat(40))
    }

    @Test
    fun fortyOneCharacterNameShowsErrorWithoutCallingCallback() {
        checkRejectedName("A".repeat(41), "Team name must be 1–40 characters")
    }

    private fun showValidationScreen(submittedNames: MutableList<String>) {
        composeRule.setContent {
            MatchdayTheme {
                TeamsScreen(
                    tournamentName = "Cup",
                    teams = emptyList(),
                    onAddTeam = { name ->
                        submittedNames.add(name)
                        Result.success("team-id")
                    },
                    onBack = {}
                )
            }
        }
    }

    private fun checkRejectedName(input: String, expectedMessage: String) {
        val submittedNames = mutableListOf<String>()
        showValidationScreen(submittedNames)
        composeRule.onNodeWithText("Add Team").performClick()
        val field = composeRule.onNode(hasSetTextAction())
        if (input.isNotEmpty()) field.performTextInput(input)
        composeRule.onNodeWithText("Save").performClick()
        composeRule.onNodeWithText(expectedMessage).assertIsDisplayed()
        composeRule.onNode(isDialog()).assertIsDisplayed()
        composeRule.runOnIdle { assertTrue(submittedNames.isEmpty()) }
        field.performTextClearance()
        field.performTextInput("Chelsea")
        composeRule.onNodeWithText(expectedMessage).assertDoesNotExist()
    }

    private fun checkAcceptedName(input: String, expectedName: String) {
        val submittedNames = mutableListOf<String>()
        showValidationScreen(submittedNames)
        composeRule.onNodeWithText("Add Team").performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput(input)
        composeRule.onNodeWithText("Save").performClick()
        composeRule.runOnIdle { assertEquals(listOf(expectedName), submittedNames) }
        composeRule.onNode(isDialog()).assertDoesNotExist()
    }

    @Test
    fun titleIsShownOnceAndBackCallsCallbackOnce() {
        var backCalls = 0

        composeRule.setContent {
            MaterialTheme {
                TeamsScreen(
                    tournamentName = "SDU FIFA Cup",
                    teams = emptyList(),
                    onAddTeam = { Result.success("team-id") },
                    onBack = { backCalls++ }
                )
            }
        }

        composeRule.onAllNodesWithText("SDU FIFA Cup").assertCountEquals(1)
        composeRule.onNodeWithText("SDU FIFA Cup").assertIsDisplayed()
        composeRule.runOnIdle { assertEquals(0, backCalls) }
        composeRule.onNodeWithText("Back").performClick()
        composeRule.runOnIdle { assertEquals(1, backCalls) }
    }

    @Test
    fun emptyTeamsShowsInstructionAndAllowsOpeningAddDialog() {
        composeRule.setContent {
            MaterialTheme {
                TeamsScreen(
                    tournamentName = "Cup",
                    teams = emptyList(),
                    onAddTeam = { Result.success("team-id") },
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText(
            "No teams yet\nTap Add Team to add your first team."
        ).assertIsDisplayed()
        composeRule.onNodeWithText("Add Team")
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()
        composeRule.onNode(isDialog()).assertIsDisplayed()
    }

    @Test
    fun nonEmptyTeamsShowsTeamNamesWithoutEmptyState() {
        composeRule.setContent {
            MaterialTheme {
                TeamsScreen(
                    tournamentName = "Cup",
                    teams = listOf(
                        Team("arsenal-id", "cup-id", "Arsenal"),
                        Team("chelsea-id", "cup-id", "Chelsea")
                    ),
                    onAddTeam = { Result.success("team-id") },
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Arsenal").assertIsDisplayed()
        composeRule.onNodeWithText("Chelsea").assertIsDisplayed()
        composeRule.onNodeWithText("No teams yet", substring = true).assertDoesNotExist()
        composeRule.onNodeWithText("Add Team").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun duplicateErrorPreservesNameAndAllowsRetryAfterEditing() {
        val submittedNames = mutableListOf<String>()
        val duplicateMessage = "A team with this name already exists in this tournament."

        composeRule.setContent {
            MaterialTheme {
                TeamsScreen(
                    tournamentName = "Cup",
                    teams = listOf(Team("arsenal-id", "cup-id", "Arsenal")),
                    onAddTeam = { name ->
                        submittedNames.add(name)
                        if (submittedNames.size == 1) {
                            Result.failure(IllegalArgumentException(duplicateMessage))
                        } else {
                            Result.success("chelsea-id")
                        }
                    },
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Add Team").performClick()
        val nameField = composeRule.onNode(hasSetTextAction())
        nameField.performTextInput("arsenal")
        composeRule.onNodeWithText("Save").performClick()

        composeRule.onNode(isDialog()).assertIsDisplayed()
        composeRule.onNode(
            hasText(duplicateMessage) and hasAnyAncestor(isDialog())
        ).assertIsDisplayed()
        nameField.assertTextEquals("Team name", "arsenal")
        composeRule.runOnIdle {
            assertEquals(listOf("arsenal"), submittedNames)
        }

        nameField.performTextReplacement("Chelsea")
        composeRule.onNodeWithText(duplicateMessage).assertDoesNotExist()
        composeRule.onNodeWithText("Save").performClick()

        composeRule.runOnIdle {
            assertEquals(listOf("arsenal", "Chelsea"), submittedNames)
        }
        composeRule.onNode(isDialog()).assertDoesNotExist()
    }

    @Test
    fun successfulSaveClosesDialogAndClearsFieldWhenReopened() {
        val submittedNames = mutableListOf<String>()

        composeRule.setContent {
            MaterialTheme {
                TeamsScreen(
                    tournamentName = "Cup",
                    teams = emptyList(),
                    onAddTeam = { name ->
                        submittedNames.add(name)
                        Result.success("chelsea-id")
                    },
                    onBack = {}
                )
            }
        }

        composeRule.onNodeWithText("Add Team").performClick()
        // Establish a previous message so that reopening also checks its removal.
        composeRule.onNodeWithText("Save").performClick()
        composeRule.onNodeWithText("Enter team name").assertIsDisplayed()
        composeRule.onNode(hasSetTextAction()).performTextInput("Chelsea")
        composeRule.onNodeWithText("Save").performClick()

        composeRule.runOnIdle {
            assertEquals(listOf("Chelsea"), submittedNames)
        }
        composeRule.onNode(isDialog()).assertDoesNotExist()

        composeRule.onNodeWithText("Add Team").performClick()
        composeRule.onNode(isDialog()).assertIsDisplayed()
        composeRule.onNode(hasSetTextAction()).assertTextEquals("Team name", "")
        composeRule.onNodeWithText("Enter team name").assertDoesNotExist()
        composeRule.onNodeWithText("Team added").assertDoesNotExist()
    }
}
