package com.example.matchday.domain.model

data class AppData(
    val tournaments: List<Tournament> = emptyList(),
    val teams: List<Team> = emptyList(),
    val matches: List<Match> = emptyList()
)
