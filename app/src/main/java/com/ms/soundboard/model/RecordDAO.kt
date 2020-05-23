package com.ms.soundboard.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecordDAO {

    @Query("SELECT * FROM record_table ORDER BY title ASC")
    fun getAllOrderedByTitleASC() : LiveData<List<Record>>

    @Query("SELECT * FROM record_table")
    fun getAll() : LiveData<List<Record>>

    @Query("SELECT * FROM record_table WHERE title = :titleRecord")
    fun getAllByTitle(titleRecord: String): LiveData<List<Record>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: Record)

    @Query("DELETE FROM record_table")
    suspend fun deleteAll()
}