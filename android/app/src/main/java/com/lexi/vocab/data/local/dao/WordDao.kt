package com.lexi.vocab.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.lexi.vocab.data.local.entities.DefinitionEntity
import com.lexi.vocab.data.local.entities.WordEntity
import kotlinx.coroutines.flow.Flow

data class WordWithDefinitions(
    @Embedded val word: WordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "wordId"
    )
    val definitions: List<DefinitionEntity>
)

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWords(words: List<WordEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefinitions(defs: List<DefinitionEntity>)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    @Transaction
    @Query("SELECT * FROM words WHERE id = :wordId")
    fun observeWordWithDefinitions(wordId: Long): Flow<WordWithDefinitions?>

    @Transaction
    @Query("SELECT * FROM words WHERE id IN (:wordIds)")
    suspend fun getWordsWithDefinitions(wordIds: List<Long>): List<WordWithDefinitions>

    @Transaction
    @Query(
        "SELECT * FROM words WHERE word LIKE '%' || :query || '%' " +
        "ORDER BY frequencyRank ASC LIMIT 100"
    )
    fun searchWords(query: String): Flow<List<WordWithDefinitions>>

    @Transaction
    @Query("SELECT * FROM words ORDER BY frequencyRank ASC")
    fun observeAllWords(): Flow<List<WordWithDefinitions>>

    @Query("SELECT id FROM words ORDER BY frequencyRank ASC")
    suspend fun getAllWordIdsOrderedByFrequency(): List<Long>
}
