package com.jumparoundcreations.mva_sugarcounter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "calories_table")
data class EntryCalories(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "currentTimestamp") override val currentTimestamp: Long,
    @ColumnInfo(name = "date") override val date: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "caloriesTotal") val caloriesTotal: Int,
) : IEntry
