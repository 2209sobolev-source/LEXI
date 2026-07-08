package com.lexi.vocab

import android.app.Application
import com.lexi.vocab.data.repository.VocabRepository

class VocabApplication : Application() {
    val repository: VocabRepository by lazy { VocabRepository.getInstance(this) }
}
