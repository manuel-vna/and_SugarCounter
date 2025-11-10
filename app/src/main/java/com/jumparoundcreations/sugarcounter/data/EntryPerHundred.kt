package com.jumparoundcreations.sugarcounter.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.jumparoundcreations.sugarcounter.data.counterData.GramCountMode

data class EntryPerHundred(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    @ColumnInfo(name = "date")
    override val date: String,
    @ColumnInfo(name = "currentTimestamp")
    override val currentTimestamp: Long,
    @ColumnInfo(name = "category")
    override val category: String,
    @ColumnInfo(name = "entryType")
    override val entryType: GramCountMode,
    @ColumnInfo(name = "perHundredGram")
    val perHundredGram: Int,
    @ColumnInfo(name = "perHundredQuantity")
    val perHundredQuantity: Int,
    @ColumnInfo(name = "perPieceGram")
    override val gramTotal: Int
) : IEntry
