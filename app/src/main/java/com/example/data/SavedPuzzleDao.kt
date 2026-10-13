package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPuzzleDao {

    @Query("SELECT * FROM saved_puzzles ORDER BY dateCreated DESC")
    fun getAllPuzzles(): Flow<List<SavedPuzzleEntity>>

    @Query("SELECT * FROM saved_puzzles WHERE id = :id LIMIT 1")
    suspend fun getPuzzleById(id: Long): SavedPuzzleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuzzle(puzzle: SavedPuzzleEntity): Long

    @Update
    suspend fun updatePuzzle(puzzle: SavedPuzzleEntity)

    @Delete
    suspend fun deletePuzzle(puzzle: SavedPuzzleEntity)

    @Query("DELETE FROM saved_puzzles WHERE id = :id")
    suspend fun deletePuzzleById(id: Long)
}
