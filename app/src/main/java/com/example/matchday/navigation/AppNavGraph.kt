package com.example.matchday.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.matchday.viewmodel.AppViewModel

@Composable
fun AppNavGraph(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val data by appViewModel.state.collectAsStateWithLifecycle()
    val onBack: () -> Unit = { navController.popBackStack() }

    NavHost(navController, startDestination = Routes.TOURNAMENTS, modifier = modifier) {
        composable(Routes.TOURNAMENTS) {
            var error by rememberSaveable { mutableStateOf<String?>(null) }
            PlaceholderScreen("Tournaments") {
                Button(onClick = {
                    val result = appViewModel.createTournament("Sample Cup ${data.tournaments.size + 1}")
                    error = result.exceptionOrNull()?.message
                }) { Text("Create sample tournament") }
                error?.let { Text(it) }
                if (data.tournaments.isEmpty()) Text("No tournaments yet.")
                data.tournaments.forEach { tournament ->
                    Button(onClick = { navController.navigate(Routes.tournament(tournament.id)) }) {
                        Text(tournament.name)
                    }
                }
            }
        }

        val tournamentRoutes = listOf(Routes.TOURNAMENT, Routes.TEAMS, Routes.MATCHES, Routes.STANDINGS)
        tournamentRoutes.forEach { route ->
            composable(route, arguments = listOf(navArgument(Routes.TOURNAMENT_ID) {
                type = NavType.StringType
            })) { entry ->
                val id = entry.arguments?.getString(Routes.TOURNAMENT_ID)
                val tournament = data.tournaments.find { it.id == id }
                var error by rememberSaveable { mutableStateOf<String?>(null) }
                if (tournament == null) {
                    PlaceholderScreen("Tournament not found", onBack) {
                        Text("This tournament is unavailable. Data is stored only in memory.")
                    }
                } else {
                    val teams = data.teams.filter { it.tournamentId == tournament.id }
                    val matches = data.matches.filter { it.tournamentId == tournament.id }
                    val title = when (route) {
                        Routes.TEAMS -> "Teams"
                        Routes.MATCHES -> "Matches"
                        Routes.STANDINGS -> "Standings"
                        else -> "Tournament"
                    }
                    PlaceholderScreen("$title: ${tournament.name}", onBack) {
                        when (route) {
                            Routes.TOURNAMENT -> {
                                Text("Teams: ${teams.size}, matches: ${matches.size}")
                                Button(onClick = { navController.navigate(Routes.teams(tournament.id)) }) {
                                    Text("Teams")
                                }
                                Button(onClick = { navController.navigate(Routes.matches(tournament.id)) }) {
                                    Text("Matches")
                                }
                                Button(onClick = { navController.navigate(Routes.standings(tournament.id)) }) {
                                    Text("Standings")
                                }
                            }
                            Routes.TEAMS -> {
                                Button(onClick = {
                                    val result = appViewModel.addTeam(tournament.id, "Team ${teams.size + 1}")
                                    error = result.exceptionOrNull()?.message
                                }) { Text("Add sample team") }
                                if (teams.isEmpty()) Text("No teams yet.")
                                teams.forEach { Text(it.name) }
                            }
                            Routes.MATCHES -> {
                                Button(enabled = teams.size >= 2, onClick = {
                                    val result = appViewModel.createMatch(tournament.id, teams[0].id, teams[1].id)
                                    error = result.exceptionOrNull()?.message
                                }) { Text("Create sample match (first two teams)") }
                                if (teams.size < 2) Text("Add at least two teams first.")
                                if (matches.isEmpty()) Text("No matches yet.")
                                matches.forEach { match ->
                                    val home = teams.find { it.id == match.homeTeamId }?.name ?: "Unknown team"
                                    val away = teams.find { it.id == match.awayTeamId }?.name ?: "Unknown team"
                                    Button(onClick = { navController.navigate(Routes.score(match.id)) }) {
                                        Text("$home vs $away: ${match.homeScore ?: "-"}:${match.awayScore ?: "-"}")
                                    }
                                }
                            }
                            Routes.STANDINGS -> Text("Standings will be connected after P2 integration.")
                        }
                        error?.let { Text(it) }
                    }
                }
            }
        }

        composable(Routes.SCORE, arguments = listOf(navArgument(Routes.MATCH_ID) {
            type = NavType.StringType
        })) { entry ->
            val id = entry.arguments?.getString(Routes.MATCH_ID)
            val match = data.matches.find { it.id == id }
            val home = data.teams.find { it.id == match?.homeTeamId }
            val away = data.teams.find { it.id == match?.awayTeamId }
            var error by rememberSaveable { mutableStateOf<String?>(null) }
            if (match == null || home == null || away == null ||
                data.tournaments.none { it.id == match.tournamentId }
            ) {
                PlaceholderScreen("Match not found", onBack) {
                    Text("This match is unavailable. Data is stored only in memory.")
                }
            } else {
                PlaceholderScreen("Score: ${home.name} vs ${away.name}", onBack) {
                    Text("Current score: ${match.homeScore ?: "-"}:${match.awayScore ?: "-"}")
                    Button(onClick = {
                        val result = appViewModel.saveScore(match.id, 0, 0)
                        if (result.isSuccess) onBack() else error = result.exceptionOrNull()?.message
                    }) { Text("Save sample score 0:0") }
                    error?.let { Text(it) }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (onBack != null) Button(onClick = onBack) { Text("Back") }
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Text("Temporary foundation screen")
            content()
        }
    }
}
