package com.example.matchday.data.remote

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.ClubSearchException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

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
    fun combinesAliasesAndHttpsBadgeFromDifferentCatalogsForOneProviderId() = runBlocking {
        val primary = ClubDto(22, "Canonical United", teamIconUrl = "https://")
        val alternate = ClubDto(22, "Alternative United", "AU", "https://example.com/crest.svg")
        val repository = RemoteClubSearchRepository(object : ClubSearchApi {
            override suspend fun listClubs(league: String, season: Int): List<ClubDto> =
                if (league == "pl") listOf(primary) else listOf(alternate)
        })

        val expected = listOf(Club("22", "Canonical United", "https://example.com/crest.svg"))
        assertEquals(expected, repository.search("alternative"))
        assertEquals(expected, repository.search("canonical"))
    }

    @Test
    fun blankSearchSkipsNetworkAndIOExceptionBecomesNetworkFailure() = runBlocking {
        val repository = failingRepository { IOException("Offline") }

        assertEquals(emptyList<Club>(), repository.search("  / -  "))
        val failure = runCatching { repository.search("Example") }.exceptionOrNull()
        assertTrue(failure is ClubSearchException.Network)
        assertEquals("Offline", failure?.cause?.message)
    }

    @Test
    fun retainsTimeoutHttpAndDecodingFailures() = runBlocking {
        val timeout = failingRepository { SocketTimeoutException("Timed out") }
        val invalidJson = failingRepository { SerializationException("Invalid JSON") }
        val unavailable = failingRepository {
            HttpException(Response.error<ClubDto>(503, "".toResponseBody()))
        }

        assertTrue(
            runCatching { timeout.search("Club") }.exceptionOrNull() is ClubSearchException.Timeout
        )
        assertTrue(
            runCatching { invalidJson.search("Club") }.exceptionOrNull() is ClubSearchException.InvalidResponse
        )
        val failure = runCatching { unavailable.search("Club") }.exceptionOrNull()
        assertEquals(503, (failure as? ClubSearchException.Http)?.statusCode)
    }

    @Test
    fun leavesCoroutineCancellationIntact() = runBlocking {
        val cancelled = failingRepository { CancellationException("Search superseded") }

        assertTrue(runCatching { cancelled.search("Club") }.exceptionOrNull() is CancellationException)
    }

    @Test
    fun readsRetryAfterFromRateLimitedResponses() = runBlocking {
        val examples = mapOf(
            "45" to 45L,
            "Sun, 06 Nov 1994 08:49:37 GMT" to 0L,
            "-5" to null,
            "invalid" to null
        )
        examples.forEach { (header, seconds) ->
            val repository = failingRepository { rateLimitResponse(header) }
            val failure = runCatching { repository.search("Club") }.exceptionOrNull()
            assertTrue(failure is ClubSearchException.RateLimited)
            assertEquals(seconds, (failure as? ClubSearchException.RateLimited)?.retryAfterSeconds)
        }
    }

    private fun rateLimitResponse(retryAfter: String): HttpException {
        val response = okhttp3.Response.Builder()
            .request(Request.Builder().url("https://api.openligadb.de/").build())
            .protocol(Protocol.HTTP_1_1)
            .code(429)
            .message("Too Many Requests")
            .header("Retry-After", retryAfter)
            .build()
        return HttpException(Response.error<ClubDto>("".toResponseBody(), response))
    }

    private fun failingRepository(failure: () -> Exception) =
        RemoteClubSearchRepository(object : ClubSearchApi {
            override suspend fun listClubs(league: String, season: Int): List<ClubDto> =
                throw failure()
        })
}
