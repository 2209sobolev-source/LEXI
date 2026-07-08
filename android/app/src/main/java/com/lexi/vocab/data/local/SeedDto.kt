package com.lexi.vocab.data.local

import kotlinx.serialization.Serializable

@Serializable
data class SeedDefinitionDto(
    val partOfSpeech: String,
    val definition: String,
    val example: String
)

@Serializable
data class SeedWordDto(
    val word: String,
    val ipa: String,
    val frequencyRank: Int,
    val level: String,
    val definitions: List<SeedDefinitionDto>
)
