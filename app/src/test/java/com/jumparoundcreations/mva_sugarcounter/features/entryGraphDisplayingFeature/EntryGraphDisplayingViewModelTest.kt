package com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature

import com.jumparoundcreations.mva_sugarcounter.features.useCases.GetEntryGroupPerDayUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EntryGraphDisplayingViewModelTest {

    private lateinit var viewModel: EntryGraphDisplayingViewModel
    private val getEntryByCategoryUseCase = mockk<GetEntryGroupPerDayUseCase>()

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test error case of loading the graph`() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        every { getEntryByCategoryUseCase.invoke(any()) } returns flow {
            throw RuntimeException("Test error")
        }

        // create the ViewModel after setMain and stubbing
        viewModel = EntryGraphDisplayingViewModel(getEntryByCategoryUseCase)

        //Act
        // advance virtual time so the init{} coroutine runs
        advanceUntilIdle()

        // Assert
        val state = viewModel.entryGraphDisplayingStates.value
        assertTrue(state is EntryGraphDisplayingStates.Error)
        assertEquals("Test error", (state as EntryGraphDisplayingStates.Error).message)
    }
}