package com.example.matchday.domain.model

data class SavedClub(
    val id: String,
    val name: String,
    val badgeUrl: String? = null,
    val note: String = ""
)
