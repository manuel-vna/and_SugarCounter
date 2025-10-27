package com.jumparoundcreations.sugarcounter.util

import androidx.room.TypeConverter
import java.util.Date

class CustomTypeConverter {

    @TypeConverter
    fun listToString(list: List<String>): String {
        return list.joinToString(separator = ",")
    }

    @TypeConverter
    fun stringToList(string: String): List<String> {
        return string.split(",")
    }

    @TypeConverter
    fun dateToLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun longToDate(value: Long): Date{
        return Date(value)
    }
}
