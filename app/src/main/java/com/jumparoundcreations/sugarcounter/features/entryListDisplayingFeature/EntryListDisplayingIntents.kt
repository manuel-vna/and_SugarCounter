package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature

import com.jumparoundcreations.sugarcounter.data.SugarEntry

sealed interface EntryListDisplayingIntents {
    data class OpenCardDetails(val sugarEntry: SugarEntry) : EntryListDisplayingIntents
    data class LoadEntryDataIntoCardDetails(val sugarEntry: SugarEntry) : EntryListDisplayingIntents
    data object DismissCardDetails : EntryListDisplayingIntents
    class ShowDeleteEntryConfirmation(val isShown: Boolean) : EntryListDisplayingIntents
    class DeleteEntry(val entryId: Int) : EntryListDisplayingIntents


}