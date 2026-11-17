package com.example.matchday.ui.tournaments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matchday.model.Tournament

@Composable
fun TournamentListScreen(
    tournaments: List<Tournament>,
    onCreateTournament: (String) -> Result<String>,
    onOpenTournament: (String) -> Unit
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var tournamentName by rememberSaveable { mutableStateOf("") }
    var createError by rememberSaveable { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Tournaments",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            item {
                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Create Tournament")
                }
            }
            items(tournaments, key = { it.id }) { tournament ->
                TournamentCard(
                    tournament = tournament,
                    onOpenTournament = onOpenTournament
                )
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                tournamentName = ""
                createError = null
            },
            title = { Text("Create Tournament") },
            text = {
                OutlinedTextField(
                    value = tournamentName,
                    onValueChange = {
                        tournamentName = it
                        createError = null
                    },
                    label = { Text("Tournament name") },
                    isError = createError != null,
                    supportingText = {
                        createError?.let { Text(it) }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = tournamentName.trim()
                        if (name.isBlank()) {
                            createError = "Tournament name cannot be blank"
                        } else {
                            val result = onCreateTournament(name)
                            if (result.isSuccess) {
                                showCreateDialog = false
                                tournamentName = ""
                                createError = null
                            } else {
                                createError = result.exceptionOrNull()?.message
                                    ?.takeIf { it.isNotBlank() }
                                    ?: "Cannot create tournament"
                            }
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                        tournamentName = ""
                        createError = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
