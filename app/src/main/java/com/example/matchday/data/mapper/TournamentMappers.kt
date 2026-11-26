package com.example.matchday.data.mapper

import com.example.matchday.data.local.MatchDto
import com.example.matchday.data.local.TeamDto
import com.example.matchday.data.local.TournamentDto
import com.example.matchday.data.local.TournamentSnapshotDto
import com.example.matchday.domain.model.AppData
import com.example.matchday.domain.model.Match
import com.example.matchday.domain.model.Team
import com.example.matchday.domain.model.Tournament

fun TournamentSnapshotDto.toDomain(): AppData = AppData(
    tournaments = tournaments.map { Tournament(it.id, it.name) },
    teams = teams.map { Team(it.id, it.tournamentId, it.name) },
    matches = matches.map {
        Match(it.id, it.tournamentId, it.homeTeamId, it.awayTeamId, it.homeScore, it.awayScore)
    }
)

fun AppData.toDto(): TournamentSnapshotDto = TournamentSnapshotDto(
    tournaments = tournaments.map { TournamentDto(it.id, it.name) },
    teams = teams.map { TeamDto(it.id, it.tournamentId, it.name) },
    matches = matches.map {
        MatchDto(it.id, it.tournamentId, it.homeTeamId, it.awayTeamId, it.homeScore, it.awayScore)
    }
)
