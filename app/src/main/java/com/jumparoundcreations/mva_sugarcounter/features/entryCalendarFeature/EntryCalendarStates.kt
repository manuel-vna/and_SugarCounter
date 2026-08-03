package com.jumparoundcreations.mva_sugarcounter.features.entryCalendarFeature

import com.jumparoundcreations.mva_sugarcounter.data.historyData.DateGramSummary

data class EntryCalendarStates(
    val gramSummariesPerDate: List<DateGramSummary> = emptyList()
)
