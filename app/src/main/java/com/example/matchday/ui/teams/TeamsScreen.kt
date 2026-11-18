package com.example.matchday.ui.teams

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matchday.model.Team
import com.example.matchday.ui.components.AppTopBar
import com.example.matchday.ui.components.EmptyState

@Composable
fun TeamsScreen(
    tournamentName: String,
    teams: List<Team>,
    onAddTeam: (String) -> Result<String>,
    onBack: () -> Unit
) {
    var showAddTeamDialog by rememberSaveable { mutableStateOf(false) }
    var teamName by rememberSaveable { mutableStateOf("") }
    var resultMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(title = tournamentName, onBack = onBack)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Button(onClick = {
                    resultMessage = null
                    showAddTeamDialog = true
                }) {
                    Text("Add Team")
                }
            }
            if (teams.isEmpty()) {
                item {
                    EmptyState(message = "No teams yet\nTap Add Team to add your first team.")
                }
            } else {
                items(teams, key = { team -> team.id }) { team ->
                    TeamRow(team = team)
                }
            }
        }
    }

    if (showAddTeamDialog) {
        AlertDialog(
            onDismissRequest = { showAddTeamDialog = false },
            title = { Text("Add Team") },
            text = {
                Column {
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = {
                            teamName = it
                            resultMessage = null
                        },
                        label = { Text("Team name") },
                        singleLine = true
                    )
                    resultMessage?.let { message ->
                        Text(
                            text = message,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val name = teamName.trim()
                    resultMessage = when {
                        name.isEmpty() -> "Enter team name"
                        name.length > 40 -> "Team name must be 1–40 characters"
                        else -> {
                            val result = onAddTeam(name)
                            if (result.isSuccess) {
                                teamName = ""
                                showAddTeamDialog = false
                                null
                            } else {
                                result.exceptionOrNull()?.message ?: "Cannot add team"
                            }
                        }
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTeamDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
