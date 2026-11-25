package com.example.matchday.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable data object TournamentsRoute
@Serializable data object MyClubsRoute
@Serializable data class TournamentRoute(val tournamentId: String)
@Serializable data class TeamsRoute(val tournamentId: String)
@Serializable data class MatchesRoute(val tournamentId: String)
@Serializable data class StandingsRoute(val tournamentId: String)
@Serializable data class ScoreRoute(val matchId: String)
