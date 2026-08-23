package com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode

@Entity(tableName = "sugarEntriesTable")
data class SugarEntryInt(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "currentTimestamp")
    val currentTimestamp: Long,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "entryType")
    val entryType: GramCountMode,
    @ColumnInfo(name = "gramPerHundred")
    val gramPerHundred: Int?,
    @ColumnInfo(name = "gramPerPiece")
    val gramPerPiece: Int?,
    @ColumnInfo(name = "quantity")
    val quantity: Int?,
    @ColumnInfo(name = "amount")
    val amount: Int?,
    @ColumnInfo(name = "gramTotal")
    val gramTotal: Int?,
)
