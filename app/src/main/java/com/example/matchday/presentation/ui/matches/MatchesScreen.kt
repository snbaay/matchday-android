package com.example.matchday.presentation.ui.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.res.stringResource
import com.example.matchday.R
import com.example.matchday.presentation.ui.errorText
import com.example.matchday.domain.model.Match
import com.example.matchday.domain.model.Team
import com.example.matchday.presentation.ui.theme.MatchdayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    tournamentName: String,
    teams: List<Team>,
    matches: List<Match>,
    onCreateMatch: (String, String) -> Result<String>,
    onOpenScore: (String) -> Unit,
    onBack: () -> Unit,
    onAddFromMyClubs: (() -> Unit)? = null
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
                            text = stringResource(R.string.matches),
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
                        Text(stringResource(R.string.back))
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
                Text(stringResource(R.string.create_match_button))
            }
            if (onAddFromMyClubs != null) {
                OutlinedButton(onClick = onAddFromMyClubs, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.add_from_my_clubs))
                }
            }

            if (!canCreateMatch) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Text(
                        text = stringResource(R.string.need_two_teams_count, teams.size),
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
                        val homeTeamName = teamNamesById[match.homeTeamId] ?: stringResource(R.string.unknown_team)
                        val awayTeamName = teamNamesById[match.awayTeamId] ?: stringResource(R.string.unknown_team)
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
            onCreateMatch = onCreateMatch,
            onCreated = { showCreateDialog = false }
        )
    }
}

@Composable
private fun CreateMatchDialog(
    teams: List<Team>,
    onDismiss: () -> Unit,
    onCreateMatch: (String, String) -> Result<String>,
    onCreated: () -> Unit
) {
    var homeTeamId by rememberSaveable(teams) {
        mutableStateOf(teams.firstOrNull()?.id.orEmpty())
    }
    var awayTeamId by rememberSaveable(teams) {
        mutableStateOf(teams.getOrNull(1)?.id ?: teams.firstOrNull()?.id.orEmpty())
    }
    var errorMessage by rememberSaveable { mutableStateOf<Int?>(null) }

    val isSameTeam = homeTeamId.isNotEmpty() && homeTeamId == awayTeamId
    val displayedError = if (isSameTeam) {
        R.string.home_away_different
    } else {
        errorMessage
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.create_match_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TeamSelectorField(
                    label = stringResource(R.string.home_team),
                    teams = teams,
                    selectedTeamId = homeTeamId,
                    onSelectTeam = {
                        homeTeamId = it
                        errorMessage = null
                    }
                )

                TeamSelectorField(
                    label = stringResource(R.string.away_team),
                    teams = teams,
                    selectedTeamId = awayTeamId,
                    onSelectTeam = {
                        awayTeamId = it
                        errorMessage = null
                    }
                )

                if (displayedError != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Text(
                            text = stringResource(displayedError),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSameTeam && homeTeamId.isNotBlank() && awayTeamId.isNotBlank(),
                onClick = {
                    if (homeTeamId == awayTeamId) {
                        errorMessage = R.string.home_away_different
                    } else {
                        val result = onCreateMatch(homeTeamId, awayTeamId)
                        if (result.isSuccess) {
                            errorMessage = null
                            onCreated()
                        } else {
                            errorMessage = result.exceptionOrNull().errorText()
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun TeamSelectorField(
    label: String,
    teams: List<Team>,
    selectedTeamId: String,
    onSelectTeam: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTeamName = teams.find { it.id == selectedTeamId }?.name ?: stringResource(R.string.select_team)

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
                    text = stringResource(R.string.no_matches),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (canCreateMatch) {
                        stringResource(R.string.no_matches_hint_ready)
                    } else {
                        stringResource(R.string.no_matches_hint_teams)
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
