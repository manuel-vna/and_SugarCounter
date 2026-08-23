package com.jumparoundcreations.mva_sugarcounter.data.historyData

import java.time.LocalDate

enum class DayStatus {
    LIMIT_OK,
    LIMIT_BREACHED,
    SPECIAL,
    NO_DATA
}

data class CalendarDayUi(
    val date: LocalDate,
    val isInCurrentMonth: Boolean,
    val isToday: Boolean
)

data class DateGramSummary(
    val date: String,
    val totalGram: Double?
)