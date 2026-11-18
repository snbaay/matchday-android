package com.example.matchday.presentation.ui.tournaments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.matchday.R
import com.example.matchday.domain.model.Tournament

@Composable
fun TournamentDashboardScreen(
    tournament: Tournament,
    teamCount: Int,
    matchCount: Int,
    onTeams: () -> Unit,
    onMatches: () -> Unit,
    onStandings: () -> Unit,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.back))
            }
            Text(
                text = tournament.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.team_count, teamCount),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.match_count, matchCount),
                style = MaterialTheme.typography.bodyLarge
            )
            Button(onClick = onTeams, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.teams))
            }
            Button(onClick = onMatches, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.matches))
            }
            Button(onClick = onStandings, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.standings))
            }
        }
    }
}
