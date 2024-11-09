package com.jumparoundcreations.mva_sugarcounter.data

/**
Data class that supports grouping sugar entries by day
 */
data class EntryGroup<T>(
    val date: String,
    val dayDisplayFormat: String,
    val entryList: List<T>
)
