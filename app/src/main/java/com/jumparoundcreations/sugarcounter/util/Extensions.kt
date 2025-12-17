package com.jumparoundcreations.sugarcounter.util

import com.jumparoundcreations.sugarcounter.data.EntryGroup
import com.jumparoundcreations.sugarcounter.data.EntryGroupIntTemp
import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.data.SugarEntryIntTemp

fun SugarEntry.toIntModel(): SugarEntryIntTemp =
    SugarEntryIntTemp(
        id = id,
        currentTimestamp = currentTimestamp,
        date = date,
        category = category,
        entryType = entryType,
        gram = gram.toInt(),
        quantity = quantity.toInt(),
        gramTotal = gramTotal.toInt()
    )

fun EntryGroup.toIntModel(): EntryGroupIntTemp =
    EntryGroupIntTemp(
        date = date,
        dayDisplayFormat = dayDisplayFormat,
        entryList = entryList.map { it.toIntModel() }
    )

fun List<EntryGroup>.toIntModel(): List<EntryGroupIntTemp> =
    map { it.toIntModel() }