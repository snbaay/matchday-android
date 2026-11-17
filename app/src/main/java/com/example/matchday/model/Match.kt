package com.example.matchday.model

data class Match(
    val id: String,
    val tournamentId: String,
    val homeTeamId: String,
    val awayTeamId: String,
    val homeScore: Int? = null,
    val awayScore: Int? = null
)
