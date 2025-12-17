package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature

import com.jumparoundcreations.sugarcounter.data.EntryGroup
import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode

data class EntryListDisplayingStates(
    val showCardItemBottomSheet: Boolean = false,
    val entryInCardItem: SugarEntry = SugarEntry(
        id = 0,
        currentTimestamp = 0L,
        date = "",
        category = "",
        entryType = GramCountMode.PerHundred,
        gram = 0.0,
        quantity = 0.0,
        gramTotal = 0.0
    ),
    val entryDeletionConfirmationDialogShown: Boolean = false,
    val valueCategory: String = "",
    val valueGram: String = "",
    val valueQuantity: String = "",
    val entriesGroupedPerDayCounter: List<EntryGroup> = listOf(),
    val entriesGroupedPerDayHistory: List<EntryGroup> = listOf()
)

