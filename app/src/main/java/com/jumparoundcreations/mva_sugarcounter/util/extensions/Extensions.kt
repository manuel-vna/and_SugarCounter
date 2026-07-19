package com.jumparoundcreations.mva_sugarcounter.util.extensions

import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature.data.EntryGroupInt
import com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature.data.SugarEntryInt
import kotlin.math.round

fun SugarEntry.toIntModel(): SugarEntryInt =
    SugarEntryInt(
        id = id,
        currentTimestamp = currentTimestamp,
        date = date,
        category = category,
        entryType = entryType,
        gramPerHundred = gramPerHundred.toInt(),
        gramPerPiece = gramPerPiece.toInt(),
        quantity = quantity.toInt(),
        amount = amount.toInt(),
        gramTotal = gramTotal.toInt(),
    )

fun EntryGroup.toIntModel(): EntryGroupInt =
    EntryGroupInt(
        date = date,
        dayDisplayFormat = dayDisplayFormat,
        entryList = entryList.map { it.toIntModel() },
    )

fun List<EntryGroup>.toIntModel(): List<EntryGroupInt> = map { it.toIntModel() }

fun Double.roundToOneDecimal(): Double = round(this * 10) / 10
