package com.example.matchday.ui.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matchday.model.Match
import com.example.matchday.model.Team
import com.example.matchday.ui.theme.MatchdayTheme

/**
 * Displays the matches screen with a top bar, Back button, and scrollable match list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    tournamentName: String,
    teams: List<Team>,
    matches: List<Match>,
    onCreateMatch: (String, String) -> Result<String>,
    onOpenScore: (String) -> Unit,
    onBack: () -> Unit
) {
    val teamNamesById = teams.associate { it.id to it.name }
    val canCreateMatch = teams.size >= 2
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Matches",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = tournamentName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showCreateDialog = true },
                enabled = canCreateMatch,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Create Match")
            }

            if (!canCreateMatch) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Text(
                        text = "Need at least 2 teams to create a match (currently ${teams.size}). Add teams in the Teams section first.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (matches.isEmpty()) {
                MatchesEmptyState(
                    canCreateMatch = canCreateMatch,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(matches, key = { it.id }) { match ->
                        val homeTeamName = teamNamesById[match.homeTeamId] ?: "Unknown team"
                        val awayTeamName = teamNamesById[match.awayTeamId] ?: "Unknown team"
                        MatchRow(
                            match = match,
                            homeTeamName = homeTeamName,
                            awayTeamName = awayTeamName,
                            onClick = { onOpenScore(match.id) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog && canCreateMatch) {
        CreateMatchDialog(
            teams = teams,
            onDismiss = { showCreateDialog = false },
            onConfirm = { homeId, awayId ->
                val result = onCreateMatch(homeId, awayId)
                if (result.isSuccess) {
                    showCreateDialog = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateMatchDialog(
    teams: List<Team>,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        CreateMatchDialogCard(
            teams = teams,
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}

@Composable
private fun CreateMatchDialogCard(
    teams: List<Team>,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var homeTeamId by rememberSaveable(teams) {
        mutableStateOf(teams.firstOrNull()?.id.orEmpty())
    }
    var awayTeamId by rememberSaveable(teams) {
        mutableStateOf(teams.getOrNull(1)?.id ?: teams.firstOrNull()?.id.orEmpty())
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create Match",
                style = MaterialTheme.typography.titleLarge
            )

            TeamSelectorField(
                label = "Home team",
                teams = teams,
                selectedTeamId = homeTeamId,
                onSelectTeam = { homeTeamId = it }
            )

            TeamSelectorField(
                label = "Away team",
                teams = teams,
                selectedTeamId = awayTeamId,
                onSelectTeam = { awayTeamId = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Button(
                    onClick = { onConfirm(homeTeamId, awayTeamId) }
                ) {
                    Text("Create")
                }
            }
        }
    }
}

@Composable
private fun TeamSelectorField(
    label: String,
    teams: List<Team>,
    selectedTeamId: String,
    onSelectTeam: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTeamName = teams.find { it.id == selectedTeamId }?.name ?: "Select team"

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = selectedTeamName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                teams.forEach { team ->
                    DropdownMenuItem(
                        text = { Text(team.name) },
                        onClick = {
                            onSelectTeam(team.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchesEmptyState(
    canCreateMatch: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "No matches yet",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (canCreateMatch) {
                        "Tap '+ Create Match' above to schedule the first match."
                    } else {
                        "Add at least 2 teams to start creating matches."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchesScreenPreview() {
    val previewTeams = listOf(
        Team(id = "t1", tournamentId = "tour1", name = "Real Madrid"),
        Team(id = "t2", tournamentId = "tour1", name = "Barcelona"),
        Team(id = "t3", tournamentId = "tour1", name = "Arsenal"),
        Team(id = "t4", tournamentId = "tour1", name = "Liverpool")
    )
    val previewMatches = listOf(
        Match(
            id = "m1",
            tournamentId = "tour1",
            homeTeamId = "t1",
            awayTeamId = "t2",
            homeScore = 3,
            awayScore = 1
        ),
        Match(
            id = "m2",
            tournamentId = "tour1",
            homeTeamId = "t3",
            awayTeamId = "t4"
        )
    )

    MatchdayTheme {
        MatchesScreen(
            tournamentName = "SDU FIFA Cup",
            teams = previewTeams,
            matches = previewMatches,
            onCreateMatch = { _, _ -> Result.success("m3") },
            onOpenScore = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchesScreenEmptyOneTeamPreview() {
    val previewTeams = listOf(
        Team(id = "t1", tournamentId = "tour1", name = "Real Madrid")
    )

    MatchdayTheme {
        MatchesScreen(
            tournamentName = "SDU FIFA Cup",
            teams = previewTeams,
            matches = emptyList(),
            onCreateMatch = { _, _ -> Result.success("m1") },
            onOpenScore = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateMatchDialogPreview() {
    val previewTeams = listOf(
        Team(id = "t1", tournamentId = "tour1", name = "Real Madrid"),
        Team(id = "t2", tournamentId = "tour1", name = "Barcelona"),
        Team(id = "t3", tournamentId = "tour1", name = "Arsenal")
    )

    MatchdayTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                CreateMatchDialogCard(
                    teams = previewTeams,
                    onDismiss = {},
                    onConfirm = { _, _ -> }
                )
            }
        }
    }
}
