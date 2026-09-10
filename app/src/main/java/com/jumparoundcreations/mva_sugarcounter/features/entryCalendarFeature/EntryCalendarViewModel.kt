package com.jumparoundcreations.mva_sugarcounter.features.entryCalendarFeature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate


class EntryCalendarViewModel(
    private val getGramSummaryPerDateUseCase: GetGramSummaryPerDateUseCase
) : ViewModel() {

    private val _entryCalendarStates = MutableStateFlow(EntryCalendarStates(
        gramSummariesPerDate = emptyList(),
        selectedDate = LocalDate.now(),
        selectedDayTotalGram = 0.0
    ))
    val entryCalendarStates = _entryCalendarStates.asStateFlow()

    fun onAction(action: EntryCalendarIntents) {
        when (action) {
            is EntryCalendarIntents.CalendarDaySelection ->
                setDaySelectionValues(action.date, action.totalDayGram)
        }
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

    fun setDaySelectionValues(date: LocalDate, totalDayGram: Double) {
        _entryCalendarStates.update { current ->
            current.copy(
                selectedDate = date,
                selectedDayTotalGram = totalDayGram
            )
        }
    }

}