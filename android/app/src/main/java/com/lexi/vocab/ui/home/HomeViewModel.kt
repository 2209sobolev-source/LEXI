package com.lexi.vocab.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexi.vocab.data.repository.VocabRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val dueCount: Int = 0,
    val newCount: Int = 0,
    val learnedCount: Int = 0,
    val isSeeding: Boolean = true
)

class HomeViewModel(private val repository: VocabRepository) : ViewModel() {

    private val seeded = kotlinx.coroutines.flow.MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeDueCount(),
        repository.observeNewCount(),
        repository.observeLearnedCount(),
        seeded
    ) { due, new, learned, isSeeded ->
        HomeUiState(dueCount = due, newCount = new, learnedCount = learned, isSeeding = !isSeeded)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        viewModelScope.launch {
            repository.seedIfNeeded()
            seeded.value = true
        }
    }
}
