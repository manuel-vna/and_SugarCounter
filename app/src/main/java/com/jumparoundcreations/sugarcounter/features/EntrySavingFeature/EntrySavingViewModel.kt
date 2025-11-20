package com.jumparoundcreations.sugarcounter.features.EntrySavingFeature

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

class EntrySavingViewModel : ViewModel(), KoinComponent {

    private val _entrySavingStates = MutableStateFlow(EntrySavingStates())
    val entrySavingStates = _entrySavingStates.asStateFlow()

    fun onAction(action: EntrySavingIntents) {
        when (action) {
            is EntrySavingIntents.OpenAndCloseDatePicker -> actionChangeDatePickerVisibility()
            is EntrySavingIntents.ChangeSelectedDate -> actionChangeSelectedDate(action.epochTime)
        }
    }

    fun actionChangeDatePickerVisibility() {
        _entrySavingStates.update {
            it.copy(
                datePickerShown = it.datePickerShown.not()
            )
        }
    }

    fun actionChangeSelectedDate(epochSec: Long) {
        _entrySavingStates.update { current ->
            current.copy(
                dateOfEntryEpochSec = epochSec
            )
        }
    }


}