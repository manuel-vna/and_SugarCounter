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
                actionChangeCardDetailsVisibility(action.sugarEntry)
            }

            is EntryListDisplayingIntents.Test -> {

            }
        }
    }

    fun actionChangeCardDetailsVisibility(sugarEntry: SugarEntry) {

        _entryListDisplayingStates.update {
            it.copy(
                showCardItemBottomSheet =
                    it.showCardItemBottomSheet.not()
            )
        }

        _entryListDisplayingStates.update {
            it.copy(
                entryInCardItem = sugarEntry
            )
        }

    }


}