package com.lexi.vocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.lexi.vocab.data.local.entities.ReviewLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewLogDao {

    @Insert
    suspend fun insert(log: ReviewLogEntity)

    @androidx.room.Query(
        "SELECT COUNT(*) FROM review_log WHERE reviewedAtEpochMillis >= :sinceEpochMillis"
    )
    suspend fun countSince(sinceEpochMillis: Long): Int

    @androidx.room.Query(
        "SELECT DISTINCT reviewedAtEpochMillis / 86400000 AS dayBucket FROM review_log " +
        "ORDER BY dayBucket DESC"
    )
    fun observeStudyDayBuckets(): Flow<List<Long>>
}
