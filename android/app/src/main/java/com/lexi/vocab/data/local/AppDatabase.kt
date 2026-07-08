package com.lexi.vocab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.lexi.vocab.data.local.dao.ProgressDao
import com.lexi.vocab.data.local.dao.ReviewLogDao
import com.lexi.vocab.data.local.dao.WordDao
import com.lexi.vocab.data.local.entities.CardState
import com.lexi.vocab.data.local.entities.DefinitionEntity
import com.lexi.vocab.data.local.entities.ProgressEntity
import com.lexi.vocab.data.local.entities.ReviewLogEntity
import com.lexi.vocab.data.local.entities.WordEntity

class Converters {
    @TypeConverter
    fun fromCardState(state: CardState): String = state.name

    @TypeConverter
    fun toCardState(value: String): CardState = CardState.valueOf(value)
}

@Database(
    entities = [
        WordEntity::class,
        DefinitionEntity::class,
        ProgressEntity::class,
        ReviewLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun progressDao(): ProgressDao
    abstract fun reviewLogDao(): ReviewLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lexi.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
