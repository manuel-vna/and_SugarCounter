package com.jumparoundcreations.sugarcounter.data

import com.jumparoundcreations.sugarcounter.data.counterData.GramCountMode

interface IEntry {
    val id: Int
    val date: String
    val currentTimestamp: Long
    val category: String
    val entryType: GramCountMode
    val gramTotal: Int
}