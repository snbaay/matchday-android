package com.example.matchday.data.remote

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.repository.ClubSearchRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal class RemoteClubSearchRepository(
    private val api: ClubSearchApi
) : ClubSearchRepository {
    override suspend fun search(query: String): List<Club> {
        val name = query.trim()
        if (name.isEmpty()) return emptyList()

        val clubs = coroutineScope {
            catalogSeasons.map { (league, season) ->
                async { api.listClubs(league, season) }
            }.awaitAll().flatten()
        }

        return clubs
            .filter { club ->
                club.teamName.contains(name, ignoreCase = true) ||
                    club.shortName?.contains(name, ignoreCase = true) == true
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
