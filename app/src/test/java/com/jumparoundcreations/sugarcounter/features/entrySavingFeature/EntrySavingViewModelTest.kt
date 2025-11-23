package com.jumparoundcreations.sugarcounter.features.entrySavingFeature

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class EntrySavingViewModelTest {

    private val viewModel = EntrySavingViewModel()


    @Test
    fun `test if onAction() redirects correctly so that the states are set correctly `() = runTest {

        viewModel.onAction(EntrySavingIntents.OpenAndCloseDatePicker)
        assertEquals(true, viewModel.entrySavingStates.value.datePickerShown)

        viewModel.onAction(EntrySavingIntents.OpenAndCloseDatePicker)
        assertEquals(false, viewModel.entrySavingStates.value.datePickerShown)

        //Arrange
        val newDate = 123456789L
        //Test
        viewModel.onAction(EntrySavingIntents.ChangeSelectedDate(newDate))
        assertEquals(
            newDate,
            viewModel.entrySavingStates.value.dateOfEntryEpochSec
        )

    }

}