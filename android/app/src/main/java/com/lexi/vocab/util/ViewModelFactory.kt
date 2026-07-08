package com.lexi.vocab.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lexi.vocab.data.repository.VocabRepository

class ViewModelFactory(
    private val repository: VocabRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return modelClass.getConstructor(VocabRepository::class.java).newInstance(repository) as T
    }
}
