package com.jumparoundcreations.mva_sugarcounter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode

@Entity(tableName = "entryPerPiece_table")
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
    val perPieceGram: Double,
    @ColumnInfo(name = "perPieceAmount")
    val perPieceAmount: Double,
    @ColumnInfo(name = "gramTotal")
    override val gramTotal: Double
) : IEntry
