package com.example.matchday.presentation.ui.teams

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.matchday.R
import com.example.matchday.presentation.ui.errorText
import com.example.matchday.domain.model.Team
import com.example.matchday.presentation.ui.components.AppTopBar
import com.example.matchday.presentation.ui.components.EmptyState

@Composable
fun TeamsScreen(
    tournamentName: String,
    teams: List<Team>,
    onAddTeam: (String) -> Result<String>,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState? = null,
    clubSearch: (LazyListScope.() -> Unit)? = null
) {
    var showAddTeamDialog by rememberSaveable { mutableStateOf(false) }
    var teamName by rememberSaveable { mutableStateOf("") }
    var resultMessage by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        topBar = {
            AppTopBar(title = tournamentName, onBack = onBack)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Button(onClick = {
                    resultMessage = null
                    showAddTeamDialog = true
                }) {
                    Text(stringResource(R.string.add_team_title))
                }
            }
            clubSearch?.invoke(this)
            if (teams.isEmpty()) {
                item {
                    EmptyState(message = stringResource(R.string.no_teams_instruction))
                }
            } else {
                if (clubSearch != null) {
                    item {
                        Text(
                            text = stringResource(R.string.tournament_teams),
                            modifier = Modifier.padding(top = 12.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                items(teams, key = { team -> team.id }) { team ->
                    TeamRow(team = team)
                }
            }
        }
    }

    if (showAddTeamDialog) {
        AlertDialog(
            onDismissRequest = { showAddTeamDialog = false },
            title = { Text(stringResource(R.string.add_team_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = {
                            teamName = it
                            resultMessage = null
                        },
                        label = { Text(stringResource(R.string.team_name)) },
                        singleLine = true
                    )
                    resultMessage?.let { message ->
                        Text(
                            text = stringResource(message),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val name = teamName.trim()
                    resultMessage = when {
                        name.isEmpty() -> R.string.enter_team_name
                        name.length > 40 -> R.string.team_name_limit
                        else -> {
                            val result = onAddTeam(name)
                            if (result.isSuccess) {
                                teamName = ""
                                showAddTeamDialog = false
                                null
                            } else {
                                result.exceptionOrNull().errorText()
                            }
                        }
                    }
                }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTeamDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
