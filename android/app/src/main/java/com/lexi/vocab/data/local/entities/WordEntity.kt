package com.lexi.vocab.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single headword in the offline dictionary.
 * One word can have multiple senses -> see DefinitionEntity.
 */
@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val ipa: String,
    val level: String,          // rough difficulty band: A1, A2, B1, B2
    val frequencyRank: Int
)
