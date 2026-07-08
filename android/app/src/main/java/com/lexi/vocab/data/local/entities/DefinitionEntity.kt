package com.lexi.vocab.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One sense/meaning of a word, in the Cambridge-style layout:
 * part of speech -> English definition -> example sentence.
 */
@Entity(
    tableName = "definitions",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("wordId")]
)
data class DefinitionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,
    val partOfSpeech: String,
    val definition: String,
    val example: String,
    val senseOrder: Int
)
