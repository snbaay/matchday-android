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
            .mapNotNull { club ->
                val rank = listOfNotNull(club.teamName, club.shortName)
                    .mapNotNull { matchRank(normalizeClubName(it), name) }
                    .minOrNull() ?: return@mapNotNull null
                rank to club
            }
            .sortedWith(
                compareBy<Pair<Int, ClubDto>> { (rank, _) -> rank }
                    .thenBy(String.CASE_INSENSITIVE_ORDER) { (_, club) -> club.teamName }
                    .thenBy { (_, club) -> club.teamId }
            )
            .distinctBy { (_, club) -> club.teamId }
            .map { (_, club) ->
                Club(
                    id = club.teamId.toString(),
                    name = club.teamName,
                    badgeUrl = club.teamIconUrl?.trim()?.takeIf {
                        it.startsWith("https://", ignoreCase = true)
                    }
                )
            }
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

private fun matchRank(name: String, query: String): Int? = when {
    name == query -> 0
    name.startsWith(query) || name.contains(" $query") -> 1
    name.contains(query) -> 2
    else -> null
}
