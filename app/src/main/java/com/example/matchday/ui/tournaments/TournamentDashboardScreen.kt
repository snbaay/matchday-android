package com.example.matchday.ui.tournaments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matchday.model.Tournament

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("Back")
            }
            Text(
                text = tournament.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Teams: $teamCount",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Matches: $matchCount",
                style = MaterialTheme.typography.bodyLarge
            )
            // Section navigation buttons will use the callbacks in P4-08.
        }
    }
}
