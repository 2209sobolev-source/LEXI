package com.lexi.vocab.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexi.vocab.data.local.dao.WordWithDefinitions
import com.lexi.vocab.data.model.ReviewRating
import com.lexi.vocab.data.repository.VocabRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewUiState(
    val queue: List<WordWithDefinitions> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = true
) {
    val currentWord: WordWithDefinitions? get() = queue.getOrNull(currentIndex)
    val remaining: Int get() = (queue.size - currentIndex).coerceAtLeast(0)
    val isFinished: Boolean get() = !isLoading && queue.isNotEmpty() && currentIndex >= queue.size
    val isEmpty: Boolean get() = !isLoading && queue.isEmpty()
}

class ReviewViewModel(private val repository: VocabRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val queue = repository.buildTodayQueue()
            _uiState.value = ReviewUiState(queue = queue, isLoading = false)
        }
    }

    fun flip() {
        _uiState.value = _uiState.value.copy(isFlipped = true)
    }

    fun rate(rating: ReviewRating) {
        val word = _uiState.value.currentWord ?: return
        viewModelScope.launch {
            repository.submitRating(word.word.id, rating)
            _uiState.value = _uiState.value.copy(
                currentIndex = _uiState.value.currentIndex + 1,
                isFlipped = false
            )
        }
    }
}
