package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry

sealed interface EntryListDisplayingIntents {
    data class OpenCardDetails(val sugarEntry: SugarEntry) : EntryListDisplayingIntents
    data class LoadEntryDataIntoCardDetails(val sugarEntry: SugarEntry) : EntryListDisplayingIntents
    data class EditCategory(val newCategory: String) : EntryListDisplayingIntents
    data class EditGram(val newGram: String) : EntryListDisplayingIntents
    data class EditQuantity(val newQuantity: String) : EntryListDisplayingIntents
    data object DismissCardDetails : EntryListDisplayingIntents
    class ShowDeleteEntryConfirmation(val isShown: Boolean) : EntryListDisplayingIntents
    class DeleteEntry(val entryId: Int) : EntryListDisplayingIntents
    data object EditEntryInDB : EntryListDisplayingIntents
    data object ReuseEntryForToday : EntryListDisplayingIntents
    data object ChangeSearchFieldShown : EntryListDisplayingIntents
    class ChangeSearchTextFieldText(val newText: String) : EntryListDisplayingIntents
    data object FilterEntryListInHistory : EntryListDisplayingIntents

}