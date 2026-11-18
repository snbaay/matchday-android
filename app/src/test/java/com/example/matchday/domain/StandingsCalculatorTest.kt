package com.example.matchday.domain

import com.example.matchday.model.Match
import com.example.matchday.model.Team
import org.junit.Assert.assertEquals
import org.junit.Test

class StandingsCalculatorTest {

    private val calculateStandingsUseCase = CalculateStandingsUseCase()

    @Test
    fun calculate_awardsThreePointsForWinAndZeroForLoss() {
        val teams = listOf(
            Team(id = "t1", tournamentId = "tour1", name = "Real Madrid"),
            Team(id = "t2", tournamentId = "tour1", name = "Barcelona")
        )
        val matches = listOf(
            Match(
                id = "m1",
                tournamentId = "tour1",
                homeTeamId = "t1",
                awayTeamId = "t2",
                homeScore = 3,
                awayScore = 1
            )
        )

        val rows = calculateStandingsUseCase(teams, matches)
        assertEquals(rows, StandingsCalculator.calculate(teams, matches))

        assertEquals(2, rows.size)
        val winner = rows[0]
        assertEquals("t1", winner.teamId)
        assertEquals(1, winner.played)
        assertEquals(1, winner.won)
        assertEquals(0, winner.drawn)
        assertEquals(0, winner.lost)
        assertEquals(3, winner.goalsFor)
        assertEquals(1, winner.goalsAgainst)
        assertEquals(2, winner.goalDifference)
        assertEquals(3, winner.points)

        val loser = rows[1]
        assertEquals("t2", loser.teamId)
        assertEquals(1, loser.played)
        assertEquals(0, loser.won)
        assertEquals(0, loser.drawn)
        assertEquals(1, loser.lost)
        assertEquals(1, loser.goalsFor)
        assertEquals(3, loser.goalsAgainst)
        assertEquals(-2, loser.goalDifference)
        assertEquals(0, loser.points)
    }

    @Test
    fun calculate_awardsOnePointEachForDraw() {
        val teams = listOf(
            Team(id = "t1", tournamentId = "tour1", name = "Liverpool"),
            Team(id = "t2", tournamentId = "tour1", name = "Arsenal")
        )
        val matches = listOf(
            Match(
                id = "m1",
                tournamentId = "tour1",
                homeTeamId = "t1",
                awayTeamId = "t2",
                homeScore = 2,
                awayScore = 2
            )
        )

        val rows = calculateStandingsUseCase(teams, matches)
        assertEquals(rows, StandingsCalculator.calculate(teams, matches))

        assertEquals(2, rows.size)
        assertEquals(listOf("Arsenal", "Liverpool"), rows.map { it.teamName })
        rows.forEach { row ->
            assertEquals(1, row.played)
            assertEquals(0, row.won)
            assertEquals(1, row.drawn)
            assertEquals(0, row.lost)
            assertEquals(2, row.goalsFor)
            assertEquals(2, row.goalsAgainst)
            assertEquals(0, row.goalDifference)
            assertEquals(1, row.points)
        }
    }

    @Test
    fun calculate_recalculatesWithoutDoubleCountingAfterScoreEdit() {
        val teams = listOf(
            Team(id = "t1", tournamentId = "tour1", name = "Real Madrid"),
            Team(id = "t2", tournamentId = "tour1", name = "Barcelona")
        )
        val initialMatch = Match(
            id = "m1",
            tournamentId = "tour1",
            homeTeamId = "t1",
            awayTeamId = "t2",
            homeScore = 3,
            awayScore = 1
        )
        val editedMatch = initialMatch.copy(homeScore = 0, awayScore = 0)

        val beforeEdit = StandingsCalculator.calculate(teams, listOf(initialMatch))
        assertEquals(3, beforeEdit[0].points)
        assertEquals(0, beforeEdit[1].points)

        val afterEdit = StandingsCalculator.calculate(teams, listOf(editedMatch))
        assertEquals(listOf("Barcelona", "Real Madrid"), afterEdit.map { it.teamName })
        afterEdit.forEach { row ->
            assertEquals(1, row.played)
            assertEquals(0, row.won)
            assertEquals(1, row.drawn)
            assertEquals(0, row.lost)
            assertEquals(0, row.goalsFor)
            assertEquals(0, row.goalsAgainst)
            assertEquals(0, row.goalDifference)
            assertEquals(1, row.points)
        }
    }

    @Test
    fun calculate_ignoresUnfinishedAndUnknownMatchesAndKeepsZeroMatchTeams() {
        val teams = listOf(
            Team(id = "t1", tournamentId = "tour1", name = "Arsenal"),
            Team(id = "t2", tournamentId = "tour1", name = "Liverpool"),
            Team(id = "t3", tournamentId = "tour1", name = "Chelsea")
        )
        val matches = listOf(
            Match(id = "m1", tournamentId = "tour1", homeTeamId = "t1", awayTeamId = "t2"),
            Match(
                id = "m2",
                tournamentId = "tour1",
                homeTeamId = "t2",
                awayTeamId = "t3",
                homeScore = 2,
                awayScore = null
            ),
            Match(
                id = "m3",
                tournamentId = "tour1",
                homeTeamId = "t1",
                awayTeamId = "unknown",
                homeScore = 4,
                awayScore = 0
            ),
            Match(
                id = "m4",
                tournamentId = "tour1",
                homeTeamId = "t1",
                awayTeamId = "t2",
                homeScore = 1,
                awayScore = 0
            )
        )

        val rows = calculateStandingsUseCase(teams, matches)
        assertEquals(rows, StandingsCalculator.calculate(teams, matches))

        assertEquals(listOf("Arsenal", "Chelsea", "Liverpool"), rows.map { it.teamName })

        val arsenal = rows[0]
        assertEquals(1, arsenal.played)
        assertEquals(3, arsenal.points)
        assertEquals(1, arsenal.goalsFor)
        assertEquals(0, arsenal.goalsAgainst)

        val chelsea = rows[1]
        assertEquals(0, chelsea.played)
        assertEquals(0, chelsea.won)
        assertEquals(0, chelsea.drawn)
        assertEquals(0, chelsea.lost)
        assertEquals(0, chelsea.goalsFor)
        assertEquals(0, chelsea.goalsAgainst)
        assertEquals(0, chelsea.points)

        val liverpool = rows[2]
        assertEquals(1, liverpool.played)
        assertEquals(1, liverpool.lost)
        assertEquals(-1, liverpool.goalDifference)
        assertEquals(0, liverpool.points)
    }

    @Test
    fun calculate_sortsByPointsThenGoalDifferenceThenGoalsForThenName() {
        val zeta = Team(id = "t_zeta", tournamentId = "tour1", name = "Zeta")
        val delta = Team(id = "t_delta", tournamentId = "tour1", name = "Delta")
        val gamma = Team(id = "t_gamma", tournamentId = "tour1", name = "Gamma")
        val alpha = Team(id = "t_alpha", tournamentId = "tour1", name = "Alpha")
        val beta = Team(id = "t_beta", tournamentId = "tour1", name = "Beta")
        val omega = Team(id = "t_omega", tournamentId = "tour1", name = "Omega")

        val teams = listOf(omega, beta, alpha, gamma, delta, zeta)
        val matches = listOf(
            Match("m1", "tour1", zeta.id, omega.id, homeScore = 1, awayScore = 0),
            Match("m2", "tour1", zeta.id, delta.id, homeScore = 1, awayScore = 0),
            Match("m3", "tour1", delta.id, omega.id, homeScore = 3, awayScore = 0),
            Match("m4", "tour1", gamma.id, omega.id, homeScore = 4, awayScore = 3),
            Match("m5", "tour1", alpha.id, omega.id, homeScore = 2, awayScore = 1),
            Match("m6", "tour1", beta.id, omega.id, homeScore = 2, awayScore = 1)
        )

        val rows = calculateStandingsUseCase(teams, matches)
        assertEquals(rows, StandingsCalculator.calculate(teams, matches))

        assertEquals(
            listOf("Zeta", "Delta", "Gamma", "Alpha", "Beta", "Omega"),
            rows.map { it.teamName }
        )
        assertEquals(listOf(6, 3, 3, 3, 3, 0), rows.map { it.points })
        assertEquals(listOf(2, 2, 1, 1, 1, -7), rows.map { it.goalDifference })
        assertEquals(listOf(2, 3, 4, 2, 2, 5), rows.map { it.goalsFor })
    }
}
