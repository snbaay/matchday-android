package com.example.matchday.domain

import com.example.matchday.model.Match
import com.example.matchday.model.StandingRow
import com.example.matchday.model.Team

class CalculateStandingsUseCase {

    private class TeamStats(
        val teamId: String,
        val teamName: String
    ) {
        var played = 0
        var won = 0
        var drawn = 0
        var lost = 0
        var goalsFor = 0
        var goalsAgainst = 0
        var points = 0

        fun toStandingRow(): StandingRow = StandingRow(
            teamId = teamId,
            teamName = teamName,
            played = played,
            won = won,
            drawn = drawn,
            lost = lost,
            goalsFor = goalsFor,
            goalsAgainst = goalsAgainst,
            points = points
        )
    }

    operator fun invoke(teams: List<Team>, matches: List<Match>): List<StandingRow> {
        val statsByTeamId = LinkedHashMap<String, TeamStats>(teams.size)
        for (team in teams) {
            statsByTeamId.putIfAbsent(team.id, TeamStats(team.id, team.name))
        }

        for (match in matches) {
            val homeScore = match.homeScore ?: continue
            val awayScore = match.awayScore ?: continue
            if (homeScore < 0 || awayScore < 0 || match.homeTeamId == match.awayTeamId) {
                continue
            }

            val homeStats = statsByTeamId[match.homeTeamId] ?: continue
            val awayStats = statsByTeamId[match.awayTeamId] ?: continue

            homeStats.played += 1
            awayStats.played += 1

            homeStats.goalsFor += homeScore
            homeStats.goalsAgainst += awayScore
            awayStats.goalsFor += awayScore
            awayStats.goalsAgainst += homeScore

            when {
                homeScore > awayScore -> {
                    homeStats.won += 1
                    homeStats.points += 3
                    awayStats.lost += 1
                }
                homeScore < awayScore -> {
                    awayStats.won += 1
                    awayStats.points += 3
                    homeStats.lost += 1
                }
                else -> {
                    homeStats.drawn += 1
                    awayStats.drawn += 1
                    homeStats.points += 1
                    awayStats.points += 1
                }
            }
        }

        return statsByTeamId.values
            .map { it.toStandingRow() }
            .sortedWith(
                compareByDescending<StandingRow> { it.points }
                    .thenByDescending { it.goalDifference }
                    .thenByDescending { it.goalsFor }
                    .thenBy(String.CASE_INSENSITIVE_ORDER) { it.teamName }
                    .thenBy { it.teamName }
                    .thenBy { it.teamId }
            )
    }
}

object StandingsCalculator {
    private val calculateStandingsUseCase = CalculateStandingsUseCase()

    fun calculate(teams: List<Team>, matches: List<Match>): List<StandingRow> {
        return calculateStandingsUseCase(teams, matches)
    }
}
