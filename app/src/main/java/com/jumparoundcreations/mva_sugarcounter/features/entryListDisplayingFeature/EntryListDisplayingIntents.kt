package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry

sealed class EntryListDisplayingIntents {
    data class OpenCardDetails(
        val sugarEntry: SugarEntry,
    ) : EntryListDisplayingIntents()

    data class LoadEntryDataIntoCardDetails(
        val sugarEntry: SugarEntry,
    ) : EntryListDisplayingIntents()

    data class EditCategory(
        val newCategory: String,
    ) : EntryListDisplayingIntents()

    data class EditGram(
        val newGramPerHundred: String,
        val newGramPerPiece: String
    ) : EntryListDisplayingIntents()

    data class EditQuantity(
        val newQuantity: String,
        val newAmount: String
    ) : EntryListDisplayingIntents()

    object DismissCardDetails : EntryListDisplayingIntents()

    data class ShowDeleteEntryConfirmation(
        val isShown: Boolean,
    ) : EntryListDisplayingIntents()

    data class DeleteEntry(
        val entryId: Int,
    ) : EntryListDisplayingIntents()

    object EditEntryInDB : EntryListDisplayingIntents()

    object ReuseEntryForToday : EntryListDisplayingIntents()

    object ChangeSearchFieldShown : EntryListDisplayingIntents()

    data class ChangeSearchTextFieldText(
        val newText: String,
    ) : EntryListDisplayingIntents()

    object FilterEntryListInHistory : EntryListDisplayingIntents()

    object CloseSearchFieldAndClearText : EntryListDisplayingIntents()
}
