package com.ms.soundboard.repository

import androidx.lifecycle.LiveData
import com.ms.soundboard.model.Record
import com.ms.soundboard.model.RecordDAO

class RecordRepository(private val recordDAO: RecordDAO) {

    val allRecords: LiveData<List<Record>> = recordDAO.getAll()

    val allRecordsOrderedByTitle : LiveData<List<Record>> = recordDAO.getAllOrderedByTitleASC()

    suspend fun insert(record: Record) {
        recordDAO.insert(record)
    }

    suspend fun getAll() {
        recordDAO.getAll()
    }

    suspend fun getRecordByTitle(title: String) : LiveData<List<Record>> {

        return recordDAO.getAllByTitle(title)
    }
}