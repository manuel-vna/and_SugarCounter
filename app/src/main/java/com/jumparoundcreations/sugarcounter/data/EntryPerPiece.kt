package com.jumparoundcreations.sugarcounter.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.jumparoundcreations.sugarcounter.data.counterData.GramCountMode

data class EntryPerPiece(
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
    @ColumnInfo(name = "perPieceGram")
    val perPieceGram: Int,
    @ColumnInfo(name = "perPieceAmount")
    val perPieceAmount: Int,
    @ColumnInfo(name = "gramTotal")
    override val gramTotal: Int
) : IEntry
