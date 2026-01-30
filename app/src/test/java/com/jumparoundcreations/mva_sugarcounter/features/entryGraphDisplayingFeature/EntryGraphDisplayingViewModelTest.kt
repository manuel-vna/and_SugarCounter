package com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature

import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.features.useCases.GetEntryGroupPerDayUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class EntryGraphDisplayingViewModelTest {

    private lateinit var viewModel: EntryGraphDisplayingViewModel
    private val getEntryByCategoryUseCase = mockk<GetEntryGroupPerDayUseCase>()
    private lateinit var entryGroupList: List<EntryGroup>

    private val chocolateEntry = SugarEntry(
        id = 1,
        currentTimestamp = 0,
        date = "2025-12-20",
        category = "Chocolate Bar",
        entryType = GramCountMode.PerPiece,
        gram = 10.0,
        quantity = 1.0,
        gramTotal = 10.0
    )
    private val cookieEntry = SugarEntry(
        id = 2,
        currentTimestamp = 0,
        date = "2025-12-20", category = "Cookie",
        entryType = GramCountMode.PerPiece,
        gram = 15.0,
        quantity = 1.0,
        gramTotal = 15.0
    )
    private val yogurtEntry = SugarEntry(
        id = 3,
        currentTimestamp = 0,
        date = "2025-12-19",
        category = "Yogurt",
        entryType = GramCountMode.PerHundred,
        gram = 5.0,
        quantity = 150.0,
        gramTotal = 7.5
    )

    @Before
    fun setUp() {

        entryGroupList = listOf(
            EntryGroup(
                date = "2025-12-20",
                dayDisplayFormat = "",
                entryList = listOf(chocolateEntry, cookieEntry)
            ),
            EntryGroup(
                date = "2025-12-19",
                dayDisplayFormat = "",
                entryList = listOf(yogurtEntry)
            )
        )

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test success case of loading the graph`() = runTest {
        // Arrange
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        every { getEntryByCategoryUseCase.invoke(any()) } returns
                flowOf(entryGroupList)

        // create the ViewModel after setMain and stubbing
        viewModel = EntryGraphDisplayingViewModel(getEntryByCategoryUseCase)

        //Act
        // advance virtual time so the init{} coroutine runs
        advanceUntilIdle()

        // Assert
        val state = viewModel.entryGraphDisplayingStates.value
        assertTrue(state is EntryGraphDisplayingStates.Success)
        assertEquals(
            expected = entryGroupList,
            actual = (state as EntryGraphDisplayingStates.Success).data.entriesGroupedPerDay
        )
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