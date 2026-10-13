package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_puzzles")
data class SavedPuzzleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val initialBoard: String, // 81 digits
    val currentBoard: String, // 81 digits
    val difficulty: String,
    val isSolved: Boolean,
    val timeSpentSeconds: Long,
    val dateCreated: Long = System.currentTimeMillis()
)
