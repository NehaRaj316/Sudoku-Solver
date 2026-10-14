package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreRecordDao {

    @Query("SELECT * FROM score_records ORDER BY score DESC LIMIT 50")
    fun getAllScores(): Flow<List<ScoreRecordEntity>>

    @Query("SELECT * FROM score_records WHERE difficulty = :difficulty AND isWin = 1 ORDER BY score DESC LIMIT 1")
    fun getBestScoreForDifficulty(difficulty: String): Flow<ScoreRecordEntity?>

    @Query("SELECT * FROM score_records WHERE difficulty = :difficulty AND isWin = 1 ORDER BY timeSeconds ASC LIMIT 1")
    fun getFastestWinForDifficulty(difficulty: String): Flow<ScoreRecordEntity?>

    @Query("SELECT COUNT(*) FROM score_records WHERE isWin = 1")
    fun getTotalWins(): Flow<Int>

    @Query("SELECT MAX(score) FROM score_records")
    fun getAllTimeHighScore(): Flow<Int?>

    @Query("SELECT MAX(highestCombo) FROM score_records")
    fun getHighestAllTimeCombo(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(scoreRecord: ScoreRecordEntity): Long

    @Query("DELETE FROM score_records")
    suspend fun clearAllScores()
}
