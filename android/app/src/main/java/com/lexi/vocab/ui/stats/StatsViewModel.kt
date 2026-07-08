package com.lexi.vocab.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexi.vocab.data.repository.VocabRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class StatsUiState(
    val newCount: Int = 0,
    val learnedCount: Int = 0,
    val currentStreak: Int = 0
)

class StatsViewModel(private val repository: VocabRepository) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        repository.observeNewCount(),
        repository.observeLearnedCount(),
        repository.observeStudyDayBuckets()
    ) { newCount, learnedCount, dayBuckets ->
        StatsUiState(
            newCount = newCount,
            learnedCount = learnedCount,
            currentStreak = computeStreak(dayBuckets)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())

    private fun computeStreak(dayBuckets: List<Long>): Int {
        if (dayBuckets.isEmpty()) return 0
        val daysSet = dayBuckets.toHashSet()
        val today = LocalDate.now().toEpochDay()
        var cursor = if (daysSet.contains(today)) today else today - 1
        if (!daysSet.contains(cursor)) return 0
        var streak = 0
        while (daysSet.contains(cursor)) {
            streak++
            cursor--
        }
        return streak
    }
}
