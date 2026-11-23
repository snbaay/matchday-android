package com.example.matchday.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class ClubSearchResponseDto(
    val response: List<ClubSearchResultDto>,
    val errors: JsonElement? = null
)

@Serializable
internal data class ClubSearchResultDto(val team: ClubDto)

@Serializable
internal data class ClubDto(
    val id: Int,
    val name: String,
    val logo: String? = null,
    val national: Boolean = false
)
