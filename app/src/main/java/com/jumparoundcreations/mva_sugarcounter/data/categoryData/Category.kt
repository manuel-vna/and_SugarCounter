package com.jumparoundcreations.mva_sugarcounter.data.categoryData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "deletionCheckbox") var deletionCheckbox: Boolean = false,
    @ColumnInfo(name = "barcodeNumber") var barcodeNumber: String = ""
)