package com.jumparoundcreations.mva_sugarcounter.features.entryCalendarFeature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class EntryCalendarViewModel(
    private val getGramSummaryPerDateUseCase: GetGramSummaryPerDateUseCase
) : ViewModel() {

    private val _entryCalendarStates = MutableStateFlow(EntryCalendarStates())
    val entryCalendarStates = _entryCalendarStates.asStateFlow()

    fun onAction(action: EntryCalendarIntents) {
        // No actions currently handled in VM for calendar
    }

    init {
        getGramSummariesByDate()
    }

    fun getGramSummariesByDate() {
        viewModelScope.launch {
            getGramSummaryPerDateUseCase().collect { list ->
                _entryCalendarStates.update { current ->
                    current.copy(
                        gramSummariesPerDate = list
                    )
                }
            }
        }
    }

}