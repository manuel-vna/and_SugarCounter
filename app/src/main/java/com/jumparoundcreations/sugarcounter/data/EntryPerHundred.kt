package com.jumparoundcreations.sugarcounter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jumparoundcreations.sugarcounter.data.counterData.GramCountMode

@Entity(tableName = "entryPerHundred_table")
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
    val perHundredGram: Double,
    @ColumnInfo(name = "perHundredQuantity")
    val perHundredQuantity: Double,
    @ColumnInfo(name = "gramTotal")
    override val gramTotal: Double
) : IEntry
