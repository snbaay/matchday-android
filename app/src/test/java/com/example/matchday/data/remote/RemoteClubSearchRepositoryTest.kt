package com.example.matchday.data.remote

import com.example.matchday.domain.model.Club
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteClubSearchRepositoryTest {
    @Test
    fun findsClubsByFullOrShortNameAndDeduplicatesProviderIds() = runBlocking {
        val club = ClubDto(42, "FC Example", "Ex", "https://example.com/badge.png")
        val otherClub = ClubDto(43, "Unrelated FC", "E-X", "http://example.com/badge.png")
        val repository = RemoteClubSearchRepository(object : ClubSearchApi {
            override suspend fun listClubs(league: String, season: Int): List<ClubDto> =
                if (league == "pl") listOf(club, otherClub) else listOf(club)
        })

        assertEquals(
            listOf(Club("42", "FC Example", "https://example.com/badge.png")),
            repository.search("  eXaMp  ")
        )
        assertEquals(listOf(Club("43", "Unrelated FC", null)), repository.search("e-x"))
        assertEquals(emptyList<Club>(), repository.search("missing"))
    }

    @Test
    fun searchesWithoutAccentsAndTreatsPunctuationAsWordSeparators() = runBlocking {
        val bayern = ClubDto(40, "FC Bayern München")
        val saintGermain = ClubDto(4363, "Paris Saint-Germain")
        val repository = RemoteClubSearchRepository(object : ClubSearchApi {
            override suspend fun listClubs(league: String, season: Int): List<ClubDto> =
                listOf(bayern, saintGermain)
        })

        assertEquals(
            listOf(Club("40", "FC Bayern München", null)),
            repository.search("  BAYERN  munchen  ")
        )
        assertEquals(
            listOf(Club("4363", "Paris Saint-Germain", null)),
            repository.search("paris saint / germain")
        )
    }

    @Test
    fun ranksExactNamesBeforeWordPrefixesAndPartialMatches() = runBlocking {
        val repository = RemoteClubSearchRepository(object : ClubSearchApi {
            override suspend fun listClubs(league: String, season: Int): List<ClubDto> = listOf(
                ClubDto(3, "AA Northarsenal"),
                ClubDto(2, "Zeta Arsenal Women"),
                ClubDto(1, "FC Arsenal", "Arsenal"),
                ClubDto(4, "AFC Arsenal Academy")
            )
        })

        assertEquals(listOf("1", "4", "2", "3"), repository.search("arsenal").map(Club::id))
    }

    @Test
    fun blankSearchSkipsNetworkAndRequestFailureReachesCaller() = runBlocking {
        val repository = RemoteClubSearchRepository(object : ClubSearchApi {
            override suspend fun listClubs(league: String, season: Int): List<ClubDto> =
                throw IOException("Offline")
        })

        assertEquals(emptyList<Club>(), repository.search("  / -  "))
        val failure = runCatching { repository.search("Example") }.exceptionOrNull()
        assertTrue(failure is IOException)
        assertEquals("Offline", failure?.message)
    }
}
