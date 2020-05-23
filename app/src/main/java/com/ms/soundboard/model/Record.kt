package com.ms.soundboard.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "record_table", indices = [Index(value = ["title"], unique = true)])
data class Record(@PrimaryKey (autoGenerate = true) @ColumnInfo(name = "record_id") val recordID: Int = 0,
                  @ColumnInfo(name = "title") var title: String = "",
                  @ColumnInfo(name = "duration") var time: Long = 0,
                  @ColumnInfo(name = "source") var source: String = "",
                  @ColumnInfo (name = "category") val category: String? = null)



