package com.example.matchday.data.local

internal data class TournamentDto(val id: String, val name: String)
internal data class TeamDto(val id: String, val tournamentId: String, val name: String)
internal data class MatchDto(
    val id: String, val tournamentId: String, val homeTeamId: String, val awayTeamId: String,
    val homeScore: Int?, val awayScore: Int?
)
internal data class TournamentSnapshotDto(
    val tournaments: List<TournamentDto> = emptyList(),
    val teams: List<TeamDto> = emptyList(),
    val matches: List<MatchDto> = emptyList()
)
