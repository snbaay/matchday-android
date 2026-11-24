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

    @Query("UPDATE saved_clubs SET note = :note WHERE id = :clubId")
    suspend fun updateNote(clubId: String, note: String)

    @Query("DELETE FROM saved_clubs WHERE id = :clubId")
    suspend fun deleteById(clubId: String)
}