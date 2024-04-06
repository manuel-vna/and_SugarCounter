package com.example.mva_sugarcounter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entry_table")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "currentTimestamp") val currentTimestamp: Long,
    @ColumnInfo(name="date") val date: String,
    @ColumnInfo(name = "gramItem") val gramItem: Int,
    @ColumnInfo(name = "amount") val amount: Int,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "gramTotal") val gramTotal: Int
)