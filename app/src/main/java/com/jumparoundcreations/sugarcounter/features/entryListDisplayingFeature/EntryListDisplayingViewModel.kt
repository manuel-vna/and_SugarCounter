package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature

import androidx.lifecycle.ViewModel
import com.jumparoundcreations.sugarcounter.data.SugarEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

class EntryListDisplayingViewModel : ViewModel(), KoinComponent {

    private val _entryListDisplayingStates = MutableStateFlow(EntryListDisplayingStates())
    val entryListDisplayingStates = _entryListDisplayingStates.asStateFlow()

    fun onAction(action: EntryListDisplayingIntents) {
        when (action) {
            is EntryListDisplayingIntents.OpenCardDetails -> {
                actionShowCardDetails(action.sugarEntry)
            }

            is EntryListDisplayingIntents.LoadEntryDataIntoCardDetails -> {
                actionLoadEntryDataIntoCardDetails(action.sugarEntry)
            }

            is EntryListDisplayingIntents.DismissCardDetails -> {
                actionDismissCardDetails()
            }

            is EntryListDisplayingIntents.ShowDeleteEntryConfirmation -> {
                actionShowDeleteEntryConfirmation()
            }

            is EntryListDisplayingIntents.DeleteEntry -> {
                actionDeleteEntry(action.entryId)
            }
        }
    }

    fun actionShowCardDetails(sugarEntry: SugarEntry) {

        _entryListDisplayingStates.update {
            it.copy(
                showCardItemBottomSheet = true
            )
        }

        _entryListDisplayingStates.update {
            it.copy(
                entryInCardItem = sugarEntry
            )
        }
    }

    fun actionLoadEntryDataIntoCardDetails(sugarEntry: SugarEntry) {
        _entryListDisplayingStates.update {
            it.copy(
                valueCategory = sugarEntry.category,
                headingGram = "",
                valueGram = sugarEntry.gram.toString(),
                headingQuantity = "",
                valueQuantity = sugarEntry.gram.toString()
            )
        }
    }

    fun actionDismissCardDetails() {
        _entryListDisplayingStates.update {
            it.copy(
                showCardItemBottomSheet = false
            )
        }
    }

    fun actionShowDeleteEntryConfirmation() {
        _entryListDisplayingStates.update {
            it.copy(
                entryDeletionConfirmationDialogShown = true
            )
        }
    }

    fun actionDeleteEntry(entryId: Int) {

    }


}