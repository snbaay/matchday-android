package com.example.matchday.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.matchday.R
import com.example.matchday.presentation.ui.matches.MatchesScreen
import com.example.matchday.presentation.ui.matches.ScoreScreen
import com.example.matchday.presentation.ui.savedclubs.MyClubsScreen
import com.example.matchday.presentation.ui.standings.StandingsScreen
import com.example.matchday.presentation.ui.teams.TeamsScreen
import com.example.matchday.presentation.ui.teams.clubSearchItems
import com.example.matchday.presentation.ui.tournaments.TournamentDashboardScreen
import com.example.matchday.presentation.ui.tournaments.TournamentListScreen
import com.example.matchday.presentation.viewmodel.AppViewModel
import com.example.matchday.presentation.viewmodel.ClubSearchViewModel
import com.example.matchday.presentation.viewmodel.SavedClubsMessage
import com.example.matchday.presentation.viewmodel.SavedClubsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavGraph(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val uiState by appViewModel.uiState.collectAsStateWithLifecycle()
    val data = uiState.data
    val onEvent: (NavigationEvent) -> Unit = { event ->
        when (event) {
            is NavigationEvent.OpenTournament -> navController.navigate(TournamentRoute(event.tournamentId))
            is NavigationEvent.OpenTeams -> navController.navigate(TeamsRoute(event.tournamentId))
            NavigationEvent.OpenMyClubs -> navController.navigate(MyClubsRoute) { launchSingleTop = true }
            is NavigationEvent.OpenMatches -> navController.navigate(MatchesRoute(event.tournamentId))
            is NavigationEvent.OpenStandings -> navController.navigate(StandingsRoute(event.tournamentId))
            is NavigationEvent.OpenScore -> navController.navigate(ScoreRoute(event.matchId))
            NavigationEvent.Back -> navController.popBackStack()
        }
    }
    val onBack: () -> Unit = { onEvent(NavigationEvent.Back) }
    val onOpenMyClubs: () -> Unit = { onEvent(NavigationEvent.OpenMyClubs) }

    NavHost(navController, startDestination = TournamentsRoute, modifier = modifier) {
        composable<TournamentsRoute> {
            TournamentListScreen(
                tournaments = data.tournaments,
                onCreateTournament = appViewModel::createTournament,
                onOpenTournament = { onEvent(NavigationEvent.OpenTournament(it)) },
                onOpenMyClubs = onOpenMyClubs
            )
        }
        composable<MyClubsRoute> { entry ->
            val savedClubs = koinViewModel<SavedClubsViewModel>(viewModelStoreOwner = entry)
            val state by savedClubs.uiState.collectAsStateWithLifecycle()
            MyClubsScreen(
                state = state,
                onBack = onBack,
                onUpdateNote = savedClubs::updateNote,
                snackbarHostState = rememberSavedClubsSnackbar(savedClubs)
            )
        }
        composable<TournamentRoute> { entry ->
            val id = entry.toRoute<TournamentRoute>().tournamentId
            val tournament = data.tournaments.find { it.id == id }
            if (tournament == null) MissingItemScreen(onBack) else {
                TournamentDashboardScreen(
                    tournament = tournament,
                    teamCount = data.teams.count { it.tournamentId == id },
                    matchCount = data.matches.count { it.tournamentId == id },
                    onTeams = { onEvent(NavigationEvent.OpenTeams(id)) },
                    onMatches = { onEvent(NavigationEvent.OpenMatches(id)) },
                    onStandings = { onEvent(NavigationEvent.OpenStandings(id)) },
                    onBack = onBack
                )
            }
        }
        composable<TeamsRoute> { entry ->
            val id = entry.toRoute<TeamsRoute>().tournamentId
            val tournament = data.tournaments.find { it.id == id }
            if (tournament == null) MissingItemScreen(onBack) else {
                val clubSearch = koinViewModel<ClubSearchViewModel>(viewModelStoreOwner = entry)
                val savedClubs = koinViewModel<SavedClubsViewModel>(viewModelStoreOwner = entry)
                val searchState by clubSearch.uiState.collectAsStateWithLifecycle()
                TeamsScreen(
                    tournamentName = tournament.name,
                    teams = data.teams.filter { it.tournamentId == id },
                    onAddTeam = { appViewModel.addTeam(id, it) },
                    onBack = onBack,
                    snackbarHostState = rememberSavedClubsSnackbar(savedClubs),
                    clubSearch = {
                        item(key = "my-clubs-link") {
                            TextButton(onClick = onOpenMyClubs) {
                                Text(stringResource(R.string.my_clubs))
                            }
                        }
                        clubSearchItems(
                            state = searchState,
                            onQueryChange = clubSearch::updateQuery,
                            onSearch = clubSearch::search,
                            onRetry = clubSearch::retry,
                            onSaveClub = savedClubs::save
                        )
                    }
                )
            }
        }
        composable<MatchesRoute> { entry ->
            val id = entry.toRoute<MatchesRoute>().tournamentId
            val tournament = data.tournaments.find { it.id == id }
            if (tournament == null) MissingItemScreen(onBack) else MatchesScreen(
                tournamentName = tournament.name,
                teams = data.teams.filter { it.tournamentId == id },
                matches = data.matches.filter { it.tournamentId == id },
                onCreateMatch = { home, away -> appViewModel.createMatch(id, home, away) },
                onOpenScore = { onEvent(NavigationEvent.OpenScore(it)) },
                onBack = onBack
            )
        }
        composable<ScoreRoute> { entry ->
            val id = entry.toRoute<ScoreRoute>().matchId
            val match = data.matches.find { it.id == id }
            val home = data.teams.find { it.id == match?.homeTeamId }
            val away = data.teams.find { it.id == match?.awayTeamId }
            if (match == null || home == null || away == null) MissingItemScreen(onBack) else ScoreScreen(
                match = match,
                homeTeamName = home.name,
                awayTeamName = away.name,
                onSaveScore = { homeScore, awayScore -> appViewModel.saveScore(id, homeScore, awayScore) },
                onBack = onBack
            )
        }
        composable<StandingsRoute> { entry ->
            val id = entry.toRoute<StandingsRoute>().tournamentId
            val tournament = data.tournaments.find { it.id == id }
            if (tournament == null) MissingItemScreen(onBack) else StandingsScreen(
                tournamentName = tournament.name,
                rows = appViewModel.standingsFor(id),
                onBack = onBack
            )
        }
    }
}

@Composable
private fun rememberSavedClubsSnackbar(viewModel: SavedClubsViewModel): SnackbarHostState {
    val context = LocalContext.current
    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel, context) {
        viewModel.messages.collect { message ->
            val text = when (message) {
                is SavedClubsMessage.ClubSaved -> context.getString(R.string.saved_club_confirmation, message.name)
                SavedClubsMessage.WriteFailed -> context.getString(R.string.operation_failed)
            }
            hostState.showSnackbar(text)
        }
    }
    return hostState
}

@Composable
private fun MissingItemScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.item_unavailable))
        Button(onClick = onBack) { Text(stringResource(R.string.back)) }
    }
}
