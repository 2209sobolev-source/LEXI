package com.lexi.vocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lexi.vocab.data.local.entities.CardState
import com.lexi.vocab.data.local.entities.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(progress: List<ProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: ProgressEntity)

    @Query("SELECT * FROM progress WHERE wordId = :wordId")
    suspend fun getProgress(wordId: Long): ProgressEntity?

    /** Cards due today: already-seen cards whose next review day has arrived. */
    @Query(
        "SELECT * FROM progress WHERE nextReviewEpochDay <= :today AND state != 'NEW' " +
        "ORDER BY nextReviewEpochDay ASC"
    )
    suspend fun getDueForReview(today: Long): List<ProgressEntity>

    /** Fresh cards the user hasn't studied yet, in frequency order. */
    @Query(
        "SELECT progress.* FROM progress JOIN words ON words.id = progress.wordId " +
        "WHERE progress.state = 'NEW' ORDER BY words.frequencyRank ASC LIMIT :limit"
    )
    suspend fun getNewCards(limit: Int): List<ProgressEntity>

    @Query("SELECT COUNT(*) FROM progress WHERE nextReviewEpochDay <= :today AND state != 'NEW'")
    fun observeDueCount(today: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM progress WHERE state = 'NEW'")
    fun observeNewCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM progress WHERE state != 'NEW'")
    fun observeLearnedCount(): Flow<Int>

    @Query("SELECT * FROM progress")
    fun observeAllProgress(): Flow<List<ProgressEntity>>
}
