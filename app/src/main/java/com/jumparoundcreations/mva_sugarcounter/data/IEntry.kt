package com.jumparoundcreations.mva_sugarcounter.data

import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode

interface IEntry {
    val id: Int
    val date: String
    val currentTimestamp: Long
    val category: String
    val entryType: GramCountMode
    val gramTotal: Double
}
