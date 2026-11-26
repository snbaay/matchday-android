package com.example.matchday.presentation.ui.tournaments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.matchday.R
import com.example.matchday.presentation.extension.errorText
import com.example.matchday.domain.model.Tournament

@Composable
fun TournamentListScreen(
    tournaments: List<Tournament>,
    onCreateTournament: (String) -> Result<String>,
    onOpenTournament: (String) -> Unit,
    onOpenMyClubs: (() -> Unit)? = null
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var tournamentName by rememberSaveable { mutableStateOf("") }
    var createError by rememberSaveable { mutableStateOf<Int?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().safeDrawingPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.tournaments),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            item {
                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.create_tournament_button))
                }
            }
            if (onOpenMyClubs != null) {
                item {
                    OutlinedButton(onClick = onOpenMyClubs, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.my_clubs))
                    }
                }
            }
            if (tournaments.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.no_tournaments),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.create_first_tournament),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(tournaments, key = { it.id }) { tournament ->
                    TournamentCard(
                        tournament = tournament,
                        onOpenTournament = onOpenTournament
                    )
                }
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
            title = { Text(stringResource(R.string.create_tournament)) },
            text = {
                OutlinedTextField(
                    value = tournamentName,
                    onValueChange = {
                        tournamentName = it
                        createError = null
                    },
                    label = { Text(stringResource(R.string.tournament_name)) },
                    isError = createError != null,
                    supportingText = {
                        createError?.let { Text(stringResource(it)) }
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
                            createError = R.string.tournament_invalid
                        } else {
                            val result = onCreateTournament(name)
                            if (result.isSuccess) {
                                showCreateDialog = false
                                tournamentName = ""
                                createError = null
                            } else {
                                createError = result.exceptionOrNull().errorText()
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.create))
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
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
