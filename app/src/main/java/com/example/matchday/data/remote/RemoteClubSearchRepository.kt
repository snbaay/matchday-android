package com.example.matchday.data.remote

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.ClubSearchException
import com.example.matchday.domain.repository.ClubSearchRepository
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.Normalizer
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.SerializationException
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import retrofit2.HttpException

internal class RemoteClubSearchRepository(
    private val api: ClubSearchApi
) : ClubSearchRepository {
    override suspend fun search(query: String): List<Club> {
        val name = normalizeClubName(query)
        if (name.isEmpty()) return emptyList()

        return loadCatalogs()
            .groupBy(ClubDto::teamId)
            .values
            .mapNotNull { clubEntries ->
                val matches = clubEntries.any { entry ->
                    normalizeClubName(entry.teamName).contains(name) ||
                        entry.shortName?.let(::normalizeClubName)?.contains(name) == true
                }
                if (!matches) return@mapNotNull null
                val primary = clubEntries.first()
                val badgeUrl = clubEntries.firstNotNullOfOrNull { entry ->
                    entry.teamIconUrl?.trim()?.toHttpUrlOrNull()
                        ?.takeIf { it.isHttps }
                        ?.toString()
                }

                Club(primary.teamId.toString(), primary.teamName, badgeUrl)
            }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, Club::name).thenBy(Club::id))
    }

    private suspend fun loadCatalogs(): List<ClubDto> = supervisorScope {
        val results = catalogSeasons.map { (league, season) ->
            async {
                runCatching { api.listClubs(league, season) }
                    .onFailure { if (it is CancellationException) throw it }
            }
        }.awaitAll()

        val successes = results.mapNotNull(Result<List<ClubDto>>::getOrNull)
        if (successes.isNotEmpty()) {
            successes.flatten()
        } else {
            throw mapCatalogFailure(results.first().exceptionOrNull()!!)
        }
    }

    private fun mapCatalogFailure(error: Throwable): Throwable = when (error) {
        is CancellationException -> error
        is HttpException -> ClubSearchException.Http(error.code(), error)
        is SerializationException -> ClubSearchException.InvalidResponse(error)
        is SocketTimeoutException -> ClubSearchException.Timeout(error)
        is IOException -> ClubSearchException.Network(error)
        else -> error
    }
}

private val catalogSeasons = mapOf(
    "pl" to 2026,
    "bl1" to 2026,
    "ucl" to 2025,
    "uel2026" to 2026
)

private val combiningMarks = Regex("\\p{M}+")
private val nameSeparators = Regex("[^\\p{L}\\p{N}]+")

private fun normalizeClubName(value: String): String =
    Normalizer.normalize(value.lowercase(Locale.ROOT), Normalizer.Form.NFKD)
        .replace(combiningMarks, "")
        .replace(nameSeparators, " ")
        .trim()
