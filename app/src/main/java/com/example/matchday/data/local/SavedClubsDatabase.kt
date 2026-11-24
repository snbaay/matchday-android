package com.example.matchday.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SavedClubEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SavedClubsDatabase : RoomDatabase() {
    abstract fun savedClubDao(): SavedClubDao

    companion object {
        const val DATABASE_NAME = "saved_clubs.db"
    }
}
