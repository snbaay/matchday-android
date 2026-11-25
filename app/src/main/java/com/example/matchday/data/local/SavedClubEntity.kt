package com.example.matchday.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_clubs")
data class SavedClubEntity(
    @PrimaryKey val id: String,
    val name: String,
    val badgeUrl: String? = null,
    val note: String = ""
)
