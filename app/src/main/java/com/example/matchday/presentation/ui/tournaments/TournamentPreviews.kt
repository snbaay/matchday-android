package com.example.matchday.presentation.ui.tournaments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matchday.domain.model.Tournament
import com.example.matchday.presentation.ui.theme.MatchdayTheme

@Preview(name = "Card - long name", widthDp = 360, heightDp = 800)
@Preview(name = "Card - large font", widthDp = 360, heightDp = 800, fontScale = 1.5f)
@Composable
private fun TournamentCardPreview() {
    MatchdayTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TournamentCard(
                    tournament = Tournament(
                        id = "preview-card",
                        name = "Межуниверситетский чемпионат по футболу среди студентов Алматы"
                    ),
                    onOpenTournament = {}
                )
            }
        }
    }
}

@Preview(name = "List - empty", widthDp = 360, heightDp = 800)
@Composable
private fun TournamentListEmptyPreview() {
    MatchdayTheme {
        TournamentListScreen(
            tournaments = emptyList(),
            onCreateTournament = {
                Result.failure(IllegalArgumentException())
            },
            onOpenTournament = {}
        )
    }
}

@Preview(name = "List - several tournaments", widthDp = 360, heightDp = 800)
@Preview(name = "List - large font", widthDp = 360, heightDp = 800, fontScale = 1.5f)
@Composable
private fun TournamentListPopulatedPreview() {
    MatchdayTheme {
        TournamentListScreen(
            tournaments = listOf(
                Tournament("preview-1", "SDU FIFA Cup"),
                Tournament("preview-2", "Friends Cup"),
                Tournament(
                    "preview-3",
                    "Межуниверситетский чемпионат по футболу среди студентов Алматы"
                ),
                Tournament("preview-4", "International Student Football Championship Autumn Season"),
                Tournament("preview-5", "Weekend Cup"),
                Tournament("preview-6", "Almaty League"),
                Tournament("preview-7", "Campus Cup"),
                Tournament("preview-8", "Winter Cup")
            ),
            onCreateTournament = {
                Result.failure(IllegalArgumentException())
            },
            onOpenTournament = {}
        )
    }
}

@Preview(name = "Dashboard - long name", widthDp = 360, heightDp = 800)
@Preview(name = "Dashboard - large font", widthDp = 360, heightDp = 800, fontScale = 1.5f)
@Composable
private fun TournamentDashboardPreview() {
    MatchdayTheme {
        TournamentDashboardScreen(
            tournament = Tournament(
                id = "preview-dashboard",
                name = "Межуниверситетский чемпионат по футболу среди студентов Алматы"
            ),
            teamCount = 12,
            matchCount = 24,
            onTeams = {},
            onMatches = {},
            onStandings = {},
            onBack = {}
        )
    }
}
