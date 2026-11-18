package com.example.matchday.data.local

import com.example.matchday.domain.model.AppData
import com.example.matchday.domain.model.Match
import com.example.matchday.domain.model.Team
import com.example.matchday.domain.model.Tournament

internal fun TournamentSnapshotDto.toDomain() = AppData(
    tournaments = tournaments.map { Tournament(it.id, it.name) },
    teams = teams.map { Team(it.id, it.tournamentId, it.name) },
    matches = matches.map { Match(it.id, it.tournamentId, it.homeTeamId, it.awayTeamId, it.homeScore, it.awayScore) }
)

internal fun AppData.toDto() = TournamentSnapshotDto(
    tournaments = tournaments.map { TournamentDto(it.id, it.name) },
    teams = teams.map { TeamDto(it.id, it.tournamentId, it.name) },
    matches = matches.map { MatchDto(it.id, it.tournamentId, it.homeTeamId, it.awayTeamId, it.homeScore, it.awayScore) }
)
