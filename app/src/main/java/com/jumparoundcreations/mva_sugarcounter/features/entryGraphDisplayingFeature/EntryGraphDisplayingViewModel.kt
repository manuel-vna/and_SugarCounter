package com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.features.useCases.GetEntryGroupPerDayUseCase
import com.jumparoundcreations.mva_sugarcounter.util.TimeConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class EntryGraphDisplayingViewModel(
    val getEntryGroupPerDayUseCase: GetEntryGroupPerDayUseCase
) : ViewModel(), KoinComponent {

    val _entryGraphDisplayingStates =
        MutableStateFlow<EntryGraphDisplayingStates>(
            value = EntryGraphDisplayingStates.Loading
        )
    val entryGraphDisplayingStates = _entryGraphDisplayingStates.asStateFlow()

    init {
        getEntriesGroupedPerDay()
    }

    fun getEntriesGroupedPerDay() {
        viewModelScope.launch {
            //delay(5_000L) // for testing purposes
            getEntryGroupPerDayUseCase(
                timeFrameBeginning =
                    TimeConstants.YEAR_ONE_IN_SECONDS
            )
                .catch { throwable ->
                    _entryGraphDisplayingStates.update {
                        EntryGraphDisplayingStates.Error(
                            message = throwable.localizedMessage ?: "Unknown error"
                        )
                    }
                }
                .collect { entriesGroupedPerDay ->
                    _entryGraphDisplayingStates.update {
                        EntryGraphDisplayingStates.Success(
                            data = SuccessData(
                                entriesGroupedPerDay = entriesGroupedPerDay
                            )
                        )
                    }
                }
        }
    }


}