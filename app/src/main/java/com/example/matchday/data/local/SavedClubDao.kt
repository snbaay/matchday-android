package com.example.matchday.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedClubDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(club: SavedClubEntity)

    @Query("SELECT * FROM saved_clubs ORDER BY name COLLATE NOCASE ASC, id ASC")
    fun observeAll(): Flow<List<SavedClubEntity>>
}
