package com.jumparoundcreations.mva_sugarcounter.util

import androidx.room.TypeConverter
import java.util.Date

class CustomTypeConverter {
    @TypeConverter
    fun listToString(list: List<String>): String = list.joinToString(separator = ",")

    @TypeConverter
    fun stringToList(string: String): List<String> = string.split(",")

    @TypeConverter
    fun dateToLong(date: Date): Long = date.time

    @TypeConverter
    fun longToDate(value: Long): Date = Date(value)
}
