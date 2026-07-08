package com.lexi.vocab.data.repository

import android.content.Context
import com.lexi.vocab.data.local.AppDatabase
import com.lexi.vocab.data.local.SeedWordDto
import com.lexi.vocab.data.local.dao.WordWithDefinitions
import com.lexi.vocab.data.local.entities.DefinitionEntity
import com.lexi.vocab.data.local.entities.ProgressEntity
import com.lexi.vocab.data.local.entities.ReviewLogEntity
import com.lexi.vocab.data.local.entities.WordEntity
import com.lexi.vocab.data.model.ReviewRating
import com.lexi.vocab.srs.Sm2Scheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import java.time.LocalDate

class VocabRepository(
    private val context: Context,
    private val db: AppDatabase
) {
    private val wordDao = db.wordDao()
    private val progressDao = db.progressDao()
    private val reviewLogDao = db.reviewLogDao()
    private val json = Json { ignoreUnknownKeys = true }

    /** Populates the local database from the bundled dictionary on first launch. */
    suspend fun seedIfNeeded() {
        if (wordDao.getWordCount() > 0) return

        val jsonText = context.assets.open("dictionary_seed.json")
            .bufferedReader()
            .use { it.readText() }
        val seedWords: List<SeedWordDto> = json.decodeFromString(jsonText)

        for (chunk in seedWords.chunked(200)) {
            val wordEntities = chunk.map {
                WordEntity(word = it.word, ipa = it.ipa, level = it.level, frequencyRank = it.frequencyRank)
            }
            val ids = wordDao.insertWords(wordEntities)

            val defs = mutableListOf<DefinitionEntity>()
            val progressList = mutableListOf<ProgressEntity>()
            chunk.forEachIndexed { index, dto ->
                val wordId = ids[index]
                dto.definitions.forEachIndexed { senseIndex, sense ->
                    defs.add(
                        DefinitionEntity(
                            wordId = wordId,
                            partOfSpeech = sense.partOfSpeech,
                            definition = sense.definition,
                            example = sense.example,
                            senseOrder = senseIndex
                        )
                    )
                }
                progressList.add(ProgressEntity(wordId = wordId))
            }
            wordDao.insertDefinitions(defs)
            progressDao.insertAll(progressList)
        }
    }

    /** Builds today's study queue: overdue review cards first, then a batch of new cards. */
    suspend fun buildTodayQueue(maxNewCards: Int = 20): List<WordWithDefinitions> {
        val today = LocalDate.now().toEpochDay()
        val dueIds = progressDao.getDueForReview(today).map { it.wordId }
        val newIds = progressDao.getNewCards(maxNewCards).map { it.wordId }
        val orderedIds = (dueIds + newIds).distinct()
        if (orderedIds.isEmpty()) return emptyList()
        val words = wordDao.getWordsWithDefinitions(orderedIds)
        val orderIndex = orderedIds.withIndex().associate { (i, id) -> id to i }
        return words.sortedBy { orderIndex[it.word.id] }
    }

    suspend fun submitRating(wordId: Long, rating: ReviewRating) {
        val today = LocalDate.now().toEpochDay()
        val current = progressDao.getProgress(wordId) ?: ProgressEntity(wordId = wordId)
        val updated = Sm2Scheduler.schedule(current, rating, today)
        progressDao.upsert(updated)
        reviewLogDao.insert(
            ReviewLogEntity(
                wordId = wordId,
                reviewedAtEpochMillis = System.currentTimeMillis(),
                rating = rating.name
            )
        )
    }

    fun observeDueCount(): Flow<Int> = progressDao.observeDueCount(LocalDate.now().toEpochDay())
    fun observeNewCount(): Flow<Int> = progressDao.observeNewCount()
    fun observeLearnedCount(): Flow<Int> = progressDao.observeLearnedCount()
    fun observeAllWords(): Flow<List<WordWithDefinitions>> = wordDao.observeAllWords()
    fun searchWords(query: String): Flow<List<WordWithDefinitions>> = wordDao.searchWords(query)
    fun observeStudyDayBuckets(): Flow<List<Long>> = reviewLogDao.observeStudyDayBuckets()

    companion object {
        @Volatile private var INSTANCE: VocabRepository? = null

        fun getInstance(context: Context): VocabRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VocabRepository(
                    context.applicationContext,
                    AppDatabase.getInstance(context)
                ).also { INSTANCE = it }
            }
        }
    }
}
