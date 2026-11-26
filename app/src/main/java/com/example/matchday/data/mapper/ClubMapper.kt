package com.example.matchday.data.mapper

import com.example.matchday.data.remote.ClubDto
import com.example.matchday.domain.model.Club
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

fun List<ClubDto>.toDomainClub(): Club {
    val primary = first()
    val badgeUrl = firstNotNullOfOrNull { entry ->
        entry.teamIconUrl?.trim()?.toHttpUrlOrNull()
            ?.takeIf { it.isHttps }
            ?.toString()
    }
    return Club(
        id = primary.teamId.toString(),
        name = primary.teamName,
        badgeUrl = badgeUrl
    )
}
