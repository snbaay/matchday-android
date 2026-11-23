package com.example.matchday.data.remote

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.repository.ClubSearchRepository
import java.text.Normalizer
import java.util.Locale
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal class RemoteClubSearchRepository(
    private val api: ClubSearchApi
) : ClubSearchRepository {
    override suspend fun search(query: String): List<Club> {
        val name = normalizeClubName(query)
        if (name.isEmpty()) return emptyList()

        val clubs = coroutineScope {
            catalogSeasons.map { (league, season) ->
                async { api.listClubs(league, season) }
            }.awaitAll().flatten()
        }

        return clubs
            .filter { club ->
                normalizeClubName(club.teamName).contains(name) ||
                    club.shortName?.let(::normalizeClubName)?.contains(name) == true
            }
            .distinctBy(ClubDto::teamId)
            .map { club ->
                Club(
                    id = club.teamId.toString(),
                    name = club.teamName,
                    badgeUrl = club.teamIconUrl?.trim()?.takeIf {
                        it.startsWith("https://", ignoreCase = true)
                    }
                )
            }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, Club::name))
    }
}

private val catalogSeasons = mapOf("pl" to 2026, "bl1" to 2026, "ucl" to 2025)

private val combiningMarks = Regex("\\p{M}+")
private val nameSeparators = Regex("[^\\p{L}\\p{N}]+")

private fun normalizeClubName(value: String): String =
    Normalizer.normalize(value.lowercase(Locale.ROOT), Normalizer.Form.NFKD)
        .replace(combiningMarks, "")
        .replace(nameSeparators, " ")
        .trim()
