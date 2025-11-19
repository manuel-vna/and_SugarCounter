package com.jumparoundcreations.sugarcounter.features.EntrySavingFeature

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

class EntrySavingViewModel : ViewModel(), KoinComponent {

    private val _entrySavingStates = MutableStateFlow<EntrySavingStates>(EntrySavingStates.Idle)
    val entrySavingStates = _entrySavingStates.asStateFlow()

    fun actionChangeDatePickerVisibility(datePickerShownValue: Boolean) {
        _entrySavingStates.update { current ->
            if (current is EntrySavingStates.SavingData) {
                current.copy(
                    data = current.data.copy(
                        datePickerShown = datePickerShownValue
                    )
                )
            } else current
        }

    }

    fun saveEntry() {
        //ToDo
    }


}