package com.example.matchday.data

import com.example.matchday.domain.repository.TournamentRepository

import com.example.matchday.data.local.InMemoryTournamentDataSource
import com.example.matchday.data.repository.InMemoryTournamentRepository

import com.example.matchday.domain.model.Match
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MatchRepositoryTest {
    private lateinit var repository: TournamentRepository
    private lateinit var tournamentId: String
    private lateinit var otherTournamentId: String
    private lateinit var homeId: String
    private lateinit var awayId: String
    private lateinit var thirdId: String
    private lateinit var otherTeamId: String

    @Before
    fun setUp() {
        repository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        tournamentId = repository.createTournament("Cup").getOrThrow()
        otherTournamentId = repository.createTournament("Other Cup").getOrThrow()
        homeId = repository.addTeam(tournamentId, "Arsenal").getOrThrow()
        awayId = repository.addTeam(tournamentId, "Barcelona").getOrThrow()
        thirdId = repository.addTeam(tournamentId, "Liverpool").getOrThrow()
        otherTeamId = repository.addTeam(otherTournamentId, "Real Madrid").getOrThrow()
    }

    @Test
    fun createMatchPublishesUnfinishedMatchAndPreservesPreviousState() {
        val previousState = repository.state.value

        val id = repository.createMatch(tournamentId, homeId, awayId).getOrThrow()

        assertTrue(id.isNotBlank())
        assertEquals(
            previousState.copy(matches = listOf(Match(id, tournamentId, homeId, awayId))),
            repository.state.value
        )
        assertTrue(previousState.matches.isEmpty())
    }

    @Test
    fun createMatchRejectsIdenticalOrMissingTeamsWithoutChangingState() {
        val previousState = repository.state.value
        for ((home, away) in listOf(homeId to homeId, "missing" to awayId, homeId to "missing")) {
            val result = repository.createMatch(tournamentId, home, away)

            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(previousState, repository.state.value)
        }
    }

    @Test
    fun createMatchRejectsTeamsFromAnotherTournament() {
        val previousState = repository.state.value
        for ((home, away) in listOf(otherTeamId to awayId, homeId to otherTeamId)) {
            val result = repository.createMatch(tournamentId, home, away)

            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(previousState, repository.state.value)
        }
        val result = repository.createMatch(otherTournamentId, homeId, awayId)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(previousState, repository.state.value)
    }

    @Test
    fun createMatchRejectsMissingTournament() {
        val previousState = repository.state.value

        val result = repository.createMatch("missing", homeId, awayId)

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(previousState, repository.state.value)
    }

    @Test
    fun createMatchRejectsRepeatedPairInEitherOrder() {
        repository.createMatch(tournamentId, homeId, awayId).getOrThrow()
        val previousState = repository.state.value

        for ((home, away) in listOf(homeId to awayId, awayId to homeId)) {
            val result = repository.createMatch(tournamentId, home, away)

            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(previousState, repository.state.value)
        }
    }

    @Test
    fun createMatchAllowsDifferentPair() {
        val firstId = repository.createMatch(tournamentId, homeId, awayId).getOrThrow()

        val secondId = repository.createMatch(tournamentId, homeId, thirdId).getOrThrow()

        assertTrue(firstId != secondId)
        assertEquals(
            listOf(
                Match(firstId, tournamentId, homeId, awayId),
                Match(secondId, tournamentId, homeId, thirdId)
            ),
            repository.state.value.matches
        )
    }

    @Test
    fun saveScoreAcceptsBoundariesAndEditsOnlyExistingMatch() {
        val id = repository.createMatch(tournamentId, homeId, awayId).getOrThrow()
        val otherId = repository.createMatch(tournamentId, homeId, thirdId).getOrThrow()
        repository.saveScore(id, 3, 1).getOrThrow()
        val previousState = repository.state.value

        repository.saveScore(id, 0, 99).getOrThrow()

        assertEquals(
            previousState.copy(matches = listOf(
                Match(id, tournamentId, homeId, awayId, 0, 99),
                Match(otherId, tournamentId, homeId, thirdId)
            )),
            repository.state.value
        )
        assertEquals(3, previousState.matches.first().homeScore)
        assertEquals(1, previousState.matches.first().awayScore)
        repository.saveScore(id, 0, 0).getOrThrow()
        assertEquals(Match(id, tournamentId, homeId, awayId, 0, 0), repository.state.value.matches.first())
        assertEquals(2, repository.state.value.matches.size)
    }

    @Test
    fun saveScoreRejectsOutOfRangeScoresWithoutChangingResult() {
        val id = repository.createMatch(tournamentId, homeId, awayId).getOrThrow()
        repository.saveScore(id, 3, 1).getOrThrow()
        val previousState = repository.state.value

        for ((home, away) in listOf(-1 to 2, 2 to -1, 100 to 2, 2 to 100)) {
            val result = repository.saveScore(id, home, away)

            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(previousState, repository.state.value)
        }
    }

    @Test
    fun saveScoreRejectsMissingMatch() {
        val previousState = repository.state.value

        val result = repository.saveScore("missing", 0, 0)

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(previousState, repository.state.value)
    }
}
