package com.example.matchday.data.repository

import com.example.matchday.data.mapper.toDomainClub
import com.example.matchday.data.remote.ClubDto
import com.example.matchday.data.remote.ClubSearchApi
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
import retrofit2.HttpException

class RemoteClubSearchRepository(
    private val api: ClubSearchApi
) : ClubSearchRepository {
    override suspend fun search(query: String): List<Club> {
        val normalizedQuery = normalizeClubName(query)
        if (normalizedQuery.isEmpty()) return emptyList()

        return loadCatalogs()
            .groupBy(ClubDto::teamId)
            .values
            .mapNotNull { clubEntries ->
                val matches = clubEntries.any { entry ->
                    normalizeClubName(entry.teamName).contains(normalizedQuery) ||
                        entry.shortName?.let(::normalizeClubName)?.contains(normalizedQuery) == true
                }
                if (matches) clubEntries.toDomainClub() else null
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

private fun normalizeClubName(value: String): String {
    val decomposed = Normalizer.normalize(value.lowercase(Locale.ROOT), Normalizer.Form.NFKD)
    return buildString(decomposed.length) {
        var needsSpace = false
        for (ch in decomposed) {
            if (isCombiningMark(ch)) continue
            if (ch.isLetterOrDigit()) {
                if (needsSpace && isNotEmpty()) {
                    append(' ')
                }
                append(ch)
                needsSpace = false
            } else {
                needsSpace = true
            }
        }
    }
}

private fun isCombiningMark(ch: Char): Boolean = when (Character.getType(ch).toByte()) {
    Character.NON_SPACING_MARK,
    Character.COMBINING_SPACING_MARK,
    Character.ENCLOSING_MARK -> true
    else -> false
}
