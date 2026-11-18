package com.example.matchday.data

import com.example.matchday.data.local.InMemoryTournamentDataSource
import com.example.matchday.domain.repository.TournamentRepository

import com.example.matchday.domain.model.AppData
import com.example.matchday.domain.model.Team
import com.example.matchday.domain.model.Tournament
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryTournamentRepositoryTest {
    @Test
    fun addTeamPublishesTrimmedTeamAndPreservesPreviousState() {
        val repository: TournamentRepository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        val tournamentId = repository.createTournament("Cup").getOrThrow()
        val previousState = repository.state.value

        val id = repository.addTeam(tournamentId, "  Real Madrid  ").getOrThrow()

        assertTrue(id.isNotBlank())
        assertEquals(
            previousState.copy(teams = listOf(Team(id, tournamentId, "Real Madrid"))),
            repository.state.value
        )
        assertTrue(previousState.teams.isEmpty())
    }

    @Test
    fun addTeamRejectsBlankNamesWithoutChangingState() {
        val repository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        val tournamentId = repository.createTournament("Cup").getOrThrow()
        val previousState = repository.state.value

        for (name in listOf("", "  \t\n  ")) {
            val result = repository.addTeam(tournamentId, name)

            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(previousState, repository.state.value)
        }
    }

    @Test
    fun addTeamRejectsDuplicatesIgnoringCaseAndSurroundingWhitespace() {
        val repository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        val tournamentId = repository.createTournament("Cup").getOrThrow()
        repository.addTeam(tournamentId, "  Real Madrid  ").getOrThrow()
        val previousState = repository.state.value

        for (name in listOf("Real Madrid", "real madrid", "  REAL MADRID\t")) {
            val result = repository.addTeam(tournamentId, name)

            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(previousState, repository.state.value)
        }
    }

    @Test
    fun addTeamAllowsSameNameInDifferentTournaments() {
        val repository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        val firstTournamentId = repository.createTournament("First Cup").getOrThrow()
        val secondTournamentId = repository.createTournament("Second Cup").getOrThrow()
        val firstId = repository.addTeam(firstTournamentId, "Arsenal").getOrThrow()

        val secondId = repository.addTeam(secondTournamentId, "arsenal").getOrThrow()

        assertTrue(firstId != secondId)
        assertEquals(
            listOf(
                Team(firstId, firstTournamentId, "Arsenal"),
                Team(secondId, secondTournamentId, "arsenal")
            ),
            repository.state.value.teams
        )
    }

    @Test
    fun addTeamRejectsMissingTournamentWithoutChangingState() {
        val repository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        repository.createTournament("Existing Cup").getOrThrow()
        val previousState = repository.state.value

        val result = repository.addTeam("missing", "Arsenal")

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(previousState, repository.state.value)
    }

    @Test
    fun addTeamEnforcesNameLengthAfterTrimming() {
        val repository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        val tournamentId = repository.createTournament("Cup").getOrThrow()
        val longestName = "A".repeat(40)
        val id = repository.addTeam(tournamentId, "  $longestName  ").getOrThrow()
        val previousState = repository.state.value

        val result = repository.addTeam(tournamentId, "A".repeat(41))

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(previousState, repository.state.value)
        assertEquals(listOf(Team(id, tournamentId, longestName)), repository.state.value.teams)
    }

    @Test
    fun createTournamentPublishesTrimmedTournamentInStateFlow() {
        val repository: TournamentRepository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        val state = repository.state
        assertEquals(AppData(), state.value)

        val id = repository.createTournament("  SDU FIFA Cup  ").getOrThrow()

        assertTrue(id.isNotBlank())
        assertEquals(
            AppData(tournaments = listOf(Tournament(id, "SDU FIFA Cup"))),
            state.value
        )
    }

    @Test
    fun creatingAnotherTournamentPreservesPreviousTournamentAndSnapshot() {
        val repository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        val firstId = repository.createTournament("First Cup").getOrThrow()
        val previousState = repository.state.value

        val secondId = repository.createTournament("Second Cup").getOrThrow()

        assertTrue(firstId != secondId)
        assertEquals(listOf(Tournament(firstId, "First Cup")), previousState.tournaments)
        assertEquals(
            listOf(Tournament(firstId, "First Cup"), Tournament(secondId, "Second Cup")),
            repository.state.value.tournaments
        )
    }

    @Test
    fun blankNamesFailWithoutChangingState() {
        val repository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        repository.createTournament("Existing Cup").getOrThrow()
        val previousState = repository.state.value

        for (name in listOf("", "  \t\n  ")) {
            val result = repository.createTournament(name)

            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(previousState, repository.state.value)
        }
    }

    @Test
    fun nameLengthLimitIsAppliedAfterTrimming() {
        val repository = InMemoryTournamentRepository(InMemoryTournamentDataSource())
        val longestName = "A".repeat(60)
        val id = repository.createTournament("  $longestName  ").getOrThrow()
        val previousState = repository.state.value

        val result = repository.createTournament("A".repeat(61))

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals(previousState, repository.state.value)
        assertEquals(listOf(Tournament(id, longestName)), repository.state.value.tournaments)
    }
}
