package com.example.matchday.navigation

object Routes {
    const val TOURNAMENT_ID = "tournamentId"
    const val MATCH_ID = "matchId"
    const val TOURNAMENTS = "tournaments"
    const val TOURNAMENT = "tournament/{tournamentId}"
    const val TEAMS = "tournament/{tournamentId}/teams"
    const val MATCHES = "tournament/{tournamentId}/matches"
    const val SCORE = "match/{matchId}/score"
    const val STANDINGS = "tournament/{tournamentId}/standings"

    fun tournament(id: String) = "tournament/$id"
    fun teams(id: String) = "tournament/$id/teams"
    fun matches(id: String) = "tournament/$id/matches"
    fun score(matchId: String) = "match/$matchId/score"
    fun standings(id: String) = "tournament/$id/standings"
}
