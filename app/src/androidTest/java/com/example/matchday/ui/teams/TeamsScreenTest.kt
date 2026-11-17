package com.example.matchday.ui.teams

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.matchday.model.Team
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TeamsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

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
    }
}
