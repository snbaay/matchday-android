package com.example.matchday.presentation.navigation

sealed interface NavigationEvent {
    data class OpenTournament(val tournamentId: String) : NavigationEvent
    data class OpenTeams(val tournamentId: String) : NavigationEvent
    data object OpenMyClubs : NavigationEvent
    data class OpenMatches(val tournamentId: String) : NavigationEvent
    data class OpenStandings(val tournamentId: String) : NavigationEvent
    data class OpenScore(val matchId: String) : NavigationEvent
    data object Back : NavigationEvent
}
