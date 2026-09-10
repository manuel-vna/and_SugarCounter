package com.jumparoundcreations.mva_sugarcounter.features.entryCalendarFeature

import com.jumparoundcreations.mva_sugarcounter.data.historyData.DateGramSummary
import java.time.LocalDate

data class EntryCalendarStates(
    val gramSummariesPerDate: List<DateGramSummary> = emptyList(),
    val selectedDate: LocalDate,
    val selectedDayTotalGram: Double
)
