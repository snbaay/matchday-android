package com.example.matchday.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RoutesTest {
    @Test fun routesCarryOnlyIds() {
        val id = "5dcf694a-05e0-4a15-ad06-baa6c0910748"
        assertNull(MyClubsRoute().tournamentId)
        assertEquals(id, MyClubsRoute(id).tournamentId)
        assertEquals(id, TournamentRoute(id).tournamentId)
        assertEquals(id, TeamsRoute(id).tournamentId)
        assertEquals(id, MatchesRoute(id).tournamentId)
        assertEquals(id, StandingsRoute(id).tournamentId)
        assertEquals(id, ScoreRoute(id).matchId)
    }
}
