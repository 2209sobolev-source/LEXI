package com.lexi.vocab.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CardState { NEW, LEARNING, REVIEW }

/**
 * Spaced-repetition state for one word, following the SM-2 algorithm
 * (the same scheme used by Anki).
 */
@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey
    val wordId: Long,
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 0,
    val repetitions: Int = 0,
    val nextReviewEpochDay: Long = 0,   // days since epoch when this card is due
    val lastReviewedEpochDay: Long = 0,
    val state: CardState = CardState.NEW
)
