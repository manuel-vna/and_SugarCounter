package com.jumparoundcreations.sugarcounter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entry_table")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "currentTimestamp") val currentTimestamp: Long,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "isPerHundred") val isPerHundred: Boolean,
    @ColumnInfo(name = "perHundredGram") val perHundredGram: Int,
    @ColumnInfo(name = "perHundredQuantity") val perHundredQuantity: Int,
    @ColumnInfo(name = "perPieceGram") val perPieceGram: Int,
    @ColumnInfo(name = "perPieceAmount") val perPieceAmount: Int,
    @ColumnInfo(name = "gramTotal") val gramTotal: Int
)