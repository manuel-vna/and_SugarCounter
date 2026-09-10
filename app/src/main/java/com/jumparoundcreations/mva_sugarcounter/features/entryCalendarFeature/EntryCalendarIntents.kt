package com.jumparoundcreations.mva_sugarcounter.features.entryCalendarFeature

import java.time.LocalDate

sealed class EntryCalendarIntents {
    data class CalendarDaySelection(
        val date: LocalDate,
        val totalDayGram: Double
    ): EntryCalendarIntents()
}