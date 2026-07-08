package com.lexi.vocab.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review_log")
data class ReviewLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,
    val reviewedAtEpochMillis: Long,
    val rating: String   // AGAIN, HARD, GOOD, EASY
)
