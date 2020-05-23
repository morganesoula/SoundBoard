package com.ms.soundboard.model

object RecordManager {

    private val records = ArrayList<Record>()

    fun getRecords() : ArrayList<Record> {
        return records
    }

    fun IDOfRecord(record: Record) = records.indexOf(record)

    fun recordsIdAsIntArray(records: List<Record>) : IntArray {
        val recordsIds = IntArray(records.size)

        for (index in 0..records.lastIndex)
            recordsIds[index] = IDOfRecord(records[index])

        return recordsIds
    }

    fun loadRecordsFromSavedState(vararg recordsIDs: Int): List<Record> {
        val recordsList : ArrayList<Record>

        if (recordsIDs.isEmpty())
            recordsList = records
        else {
            recordsList = ArrayList(recordsIDs.size)

            for (recordID in recordsIDs)
                recordsList.add(records[recordID])
        }

        return recordsList
    }


}