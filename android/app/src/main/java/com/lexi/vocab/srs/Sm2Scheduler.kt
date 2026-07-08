package com.lexi.vocab.srs

import com.lexi.vocab.data.local.entities.CardState
import com.lexi.vocab.data.local.entities.ProgressEntity
import com.lexi.vocab.data.model.ReviewRating
import kotlin.math.roundToInt

/**
 * SM-2 spaced repetition, adapted to a 4-button scheme (Again/Hard/Good/Easy)
 * the same way Anki does it. Given the current progress for a card and the
 * user's rating, returns the updated progress with a new due date.
 */
object Sm2Scheduler {

    fun schedule(current: ProgressEntity, rating: ReviewRating, todayEpochDay: Long): ProgressEntity {
        if (rating == ReviewRating.AGAIN) {
            return current.copy(
                repetitions = 0,
                intervalDays = 1,
                easeFactor = (current.easeFactor - 0.2f).coerceAtLeast(1.3f),
                nextReviewEpochDay = todayEpochDay + 1,
                lastReviewedEpochDay = todayEpochDay,
                state = CardState.LEARNING
            )
        }

        val quality = when (rating) {
            ReviewRating.HARD -> 3
            ReviewRating.GOOD -> 4
            ReviewRating.EASY -> 5
            ReviewRating.AGAIN -> 0 // unreachable, handled above
        }

        val newEase = (
            current.easeFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        ).coerceAtLeast(1.3f)

        val newRepetitions = current.repetitions + 1
        var newInterval = when {
            newRepetitions == 1 -> 1
            newRepetitions == 2 -> 6
            else -> (current.intervalDays * newEase).roundToInt()
        }
        if (rating == ReviewRating.HARD) {
            newInterval = (newInterval * 0.8).roundToInt().coerceAtLeast(1)
        }

        return current.copy(
            repetitions = newRepetitions,
            intervalDays = newInterval,
            easeFactor = newEase,
            nextReviewEpochDay = todayEpochDay + newInterval,
            lastReviewedEpochDay = todayEpochDay,
            state = CardState.REVIEW
        )
    }
}
