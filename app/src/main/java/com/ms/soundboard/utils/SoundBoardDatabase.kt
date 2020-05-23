package com.ms.soundboard.utils

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ms.soundboard.model.Record
import com.ms.soundboard.model.RecordDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@androidx.room.Database(entities = [Record::class], version = 6, exportSchema =  false)
abstract class SoundBoardDatabase : RoomDatabase() {

    abstract fun recordDAO(): RecordDAO

    private class SoundBoardDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)

            INSTANCE?.let { soundBoardDatabase ->
                scope.launch {
                    populateDB(soundBoardDatabase.recordDAO())
            } }
        }

        suspend fun populateDB(recordDAO: RecordDAO) {
            //If you want to clear DB
            //recordDAO.deleteAll()
            recordDAO.getAll()
        }
    }

    companion object {

        // Singleton way
        @Volatile
        private var INSTANCE: SoundBoardDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): SoundBoardDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoundBoardDatabase::class.java,
                    "soundboard_bdd"
                )
                    .addCallback(SoundBoardDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

}