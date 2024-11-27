package com.jumparoundcreations.mva_sugarcounter.data

interface IEntry {
    val id: Int
    val date: String
    val currentTimestamp: Long
    val category: String
}