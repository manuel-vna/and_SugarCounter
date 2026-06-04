package com.jumparoundcreations.mva_sugarcounter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode

@Entity(tableName = "sugarEntriesTable")
data class SugarEntry(
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
    @ColumnInfo(name = "gram")
    val gram: Double,
    @ColumnInfo(name = "quantity")
    val quantity: Double,
    @ColumnInfo(name = "gramTotal")
    val gramTotal: Double,
)
