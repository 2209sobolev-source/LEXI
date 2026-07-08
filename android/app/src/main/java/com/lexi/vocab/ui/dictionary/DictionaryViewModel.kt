package com.lexi.vocab.ui.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexi.vocab.data.local.dao.WordWithDefinitions
import com.lexi.vocab.data.repository.VocabRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class DictionaryUiState(
    val query: String = "",
    val results: List<WordWithDefinitions> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
class DictionaryViewModel(private val repository: VocabRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val results = _query
        .debounce(150)
        .flatMapLatest { q ->
            if (q.isBlank()) repository.observeAllWords() else repository.searchWords(q)
        }

    val uiState: StateFlow<DictionaryUiState> = combine(_query, results) { q, r ->
        DictionaryUiState(query = q, results = r)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DictionaryUiState())

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}
