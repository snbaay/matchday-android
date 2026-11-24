package com.example.matchday.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface SavedClubDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(club: SavedClubEntity)
}
