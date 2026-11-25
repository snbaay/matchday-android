package com.example.matchday.presentation.ui.savedclubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.matchday.R
import com.example.matchday.domain.model.ClubSortOrder
import com.example.matchday.domain.model.SavedClub
import com.example.matchday.domain.model.Team
import com.example.matchday.presentation.ui.components.AppTopBar
import com.example.matchday.presentation.ui.components.ClubBadge
import com.example.matchday.presentation.ui.components.EmptyState
import com.example.matchday.presentation.ui.errorText
import com.example.matchday.presentation.viewmodel.SavedClubsUiState
import kotlinx.coroutines.launch

@Composable
internal fun MyClubsScreen(
    state: SavedClubsUiState,
    onBack: () -> Unit,
    onSortOrderChange: (ClubSortOrder) -> Unit,
    onUpdateNote: (String, String) -> Unit,
    onDeleteClub: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    tournamentName: String? = null,
    tournamentTeams: List<Team> = emptyList(),
    onAddTeam: ((String) -> Result<String>)? = null,
    onOpenTeams: (() -> Unit)? = null
) {
    var editingClubId by rememberSaveable { mutableStateOf<String?>(null) }
    var removingClubId by rememberSaveable { mutableStateOf<String?>(null) }
    var noteDraft by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.my_clubs), onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state) {
                SavedClubsUiState.Loading -> item { CircularProgressIndicator() }
                SavedClubsUiState.Error -> item {
                    Text(stringResource(R.string.saved_clubs_load_failed), color = MaterialTheme.colorScheme.error)
                }
                is SavedClubsUiState.Content -> {
                    if (tournamentName != null) {
                        item {
                            Text(
                                stringResource(R.string.add_teams_to_tournament, tournamentName),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    item { SortControls(state.sortOrder, onSortOrderChange) }
                    if (state.clubs.isEmpty()) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                EmptyState(message = stringResource(R.string.saved_clubs_empty))
                                if (onOpenTeams != null) {
                                    OutlinedButton(onClick = onOpenTeams) {
                                        Text(stringResource(R.string.open_teams_search))
                                    }
                                }
                            }
                        }
                    }
                    items(state.clubs, key = { it.id }) { club ->
                        SavedClubCard(
                            club = club,
                            inTournament = tournamentTeams.any {
                                it.name.equals(club.name.trim(), ignoreCase = true)
                            },
                            onAddTeam = onAddTeam?.let { addTeam ->
                                {
                                    addTeam(club.name).onFailure { error ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(context.getString(error.errorText()))
                                        }
                                    }
                                }
                            },
                            onEdit = {
                                noteDraft = club.note
                                editingClubId = club.id
                            },
                            onRemove = { removingClubId = club.id }
                        )
                    }
                }
            }
        }
    }

    if (state is SavedClubsUiState.Content) {
        state.clubs.find { it.id == editingClubId }?.let { club ->
            AlertDialog(
                onDismissRequest = { editingClubId = null },
                title = { Text(stringResource(R.string.saved_club_edit_note)) },
                text = {
                    OutlinedTextField(
                        value = noteDraft,
                        onValueChange = { noteDraft = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.saved_club_note)) },
                        maxLines = 5
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        onUpdateNote(club.id, noteDraft)
                        editingClubId = null
                    }) { Text(stringResource(R.string.save)) }
                },
                dismissButton = {
                    TextButton(onClick = { editingClubId = null }) { Text(stringResource(R.string.cancel)) }
                }
            )
        }
        state.clubs.find { it.id == removingClubId }?.let { club ->
            AlertDialog(
                onDismissRequest = { removingClubId = null },
                title = { Text(stringResource(R.string.saved_club_remove_title, club.name)) },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteClub(club.id)
                        removingClubId = null
                    }) { Text(stringResource(R.string.saved_club_remove)) }
                },
                dismissButton = {
                    TextButton(onClick = { removingClubId = null }) { Text(stringResource(R.string.cancel)) }
                }
            )
        }
    }
}

@Composable
private fun SortControls(selected: ClubSortOrder, onSortOrderChange: (ClubSortOrder) -> Unit) {
    Column {
        Text(stringResource(R.string.saved_clubs_sort), style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ClubSortOrder.entries.forEach { order ->
                val label = when (order) {
                    ClubSortOrder.ASCENDING -> R.string.saved_clubs_ascending
                    ClubSortOrder.DESCENDING -> R.string.saved_clubs_descending
                }
                FilterChip(
                    selected = selected == order,
                    onClick = { onSortOrderChange(order) },
                    label = { Text(stringResource(label)) }
                )
            }
        }
    }
}

@Composable
private fun SavedClubCard(
    club: SavedClub,
    inTournament: Boolean,
    onAddTeam: (() -> Unit)?,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClubBadge(badgeUrl = club.badgeUrl)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(club.name, style = MaterialTheme.typography.titleMedium)
                    if (club.note.isNotBlank()) {
                        Text(club.note, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onAddTeam != null) {
                    TextButton(onClick = onAddTeam, enabled = !inTournament) {
                        Text(stringResource(if (inTournament) R.string.club_in_tournament else R.string.add_club_to_tournament))
                    }
                }
                TextButton(onClick = onEdit) { Text(stringResource(R.string.saved_club_edit_note)) }
                TextButton(onClick = onRemove) { Text(stringResource(R.string.saved_club_remove)) }
            }
        }
    }
}
