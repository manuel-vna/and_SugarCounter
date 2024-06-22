package com.example.mva_sugarcounter.data

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
    @ColumnInfo(name = "perHundredAmount") val perHundredAmount: Int,
    @ColumnInfo(name = "perPieceGram") val perPieceGram: Int, //gramItem: Int,
    @ColumnInfo(name = "perPieceQuantity") val perPieceQuantity: Int, //gramItem: Int,
    @ColumnInfo(name = "gramTotal") val gramTotal: Int
)