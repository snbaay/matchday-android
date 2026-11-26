package com.example.matchday.data.mapper

import com.example.matchday.data.local.SavedClubEntity
import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.SavedClub

fun SavedClubEntity.toDomain(): SavedClub = SavedClub(
    id = id,
    name = name,
    badgeUrl = badgeUrl,
    note = note
)

fun Club.toEntity(note: String = ""): SavedClubEntity = SavedClubEntity(
    id = id.trim(),
    name = name.trim(),
    badgeUrl = badgeUrl?.trim()?.ifEmpty { null },
    note = note.trim()
)
