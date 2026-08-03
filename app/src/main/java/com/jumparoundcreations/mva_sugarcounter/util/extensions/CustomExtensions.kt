package com.jumparoundcreations.mva_sugarcounter.util.extensions

import androidx.compose.ui.graphics.Color
import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.data.historyData.DayStatus
import com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature.data.EntryGroupInt
import com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature.data.SugarEntryInt
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

fun SugarEntry.toIntModel(): SugarEntryInt =
    SugarEntryInt(
        id = id,
        currentTimestamp = currentTimestamp,
        date = date,
        category = category,
        entryType = entryType,
        gramPerHundred = gramPerHundred?.toInt(),
        gramPerPiece = gramPerPiece?.toInt(),
        quantity = quantity?.toInt(),
        amount = amount?.toInt(),
        gramTotal = gramTotal?.toInt(),
    )

fun EntryGroup.toIntModel(): EntryGroupInt =
    EntryGroupInt(
        date = date,
        dayDisplayFormat = dayDisplayFormat,
        entryList = entryList.map { it.toIntModel() },
    )

fun List<EntryGroup>.toIntModel(): List<EntryGroupInt> = map { it.toIntModel() }

fun DayStatus.toBackgroundColor(): Color {
    return when (this) {
        DayStatus.LIMIT_OK -> Color(0xFF81C784)
        DayStatus.LIMIT_BREACHED -> Color(0xFFE57373)
        DayStatus.SPECIAL -> Color(0xFFFFF176)
        DayStatus.NO_DATA -> Color(0xFFE0E0E0)
    }
}

fun DayOfWeek.toMondayBasedIndex(): Int {
    return value - 1 // Monday = 0 ... Sunday = 6
}

fun YearMonth.formatMonthTitle(): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    return atDay(1).format(formatter)
}
