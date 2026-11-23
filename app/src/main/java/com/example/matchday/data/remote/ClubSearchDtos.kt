package com.example.matchday.data.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class ClubDto(
    val teamId: Int,
    val teamName: String,
    val shortName: String? = null,
    val teamIconUrl: String? = null
)
