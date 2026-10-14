package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_records")
data class ScoreRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val difficulty: String,
    val score: Int,
    val timeSeconds: Long,
    val mistakes: Int,
    val highestCombo: Int,
    val isWin: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)
