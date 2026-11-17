package com.example.matchday.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class RoutesTest {
    @Test
    fun routePatternsMatchContract() {
        assertEquals("tournaments", Routes.TOURNAMENTS)
        assertEquals("tournament/{tournamentId}", Routes.TOURNAMENT)
        assertEquals("tournament/{tournamentId}/teams", Routes.TEAMS)
        assertEquals("tournament/{tournamentId}/matches", Routes.MATCHES)
        assertEquals("match/{matchId}/score", Routes.SCORE)
        assertEquals("tournament/{tournamentId}/standings", Routes.STANDINGS)
    }

    @Test
    fun routeBuildersInsertOnlyIds() {
        val id = "5dcf694a-05e0-4a15-ad06-baa6c0910748"
        assertEquals("tournament/$id", Routes.tournament(id))
        assertEquals("tournament/$id/teams", Routes.teams(id))
        assertEquals("tournament/$id/matches", Routes.matches(id))
        assertEquals("match/$id/score", Routes.score(id))
        assertEquals("tournament/$id/standings", Routes.standings(id))
    }
}
