package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature

import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases.DeleteEntryUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases.EditDatabaseEntryUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases.FilterEntriesBySearchFieldUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases.ReuseEntryForTodayUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.features.useCases.GetEntryGroupPerDayUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class EntryListDisplayingViewModelTest {
    private val getEntryGroupPerDayUseCase = mockk<GetEntryGroupPerDayUseCase>()
    private val deleteEntryUseCase = mockk<DeleteEntryUseCase>(relaxed = true)
    private val editDatabaseEntryUseCase = mockk<EditDatabaseEntryUseCase>(relaxed = true)
    private val reuseEntryForTodayUseCase = mockk<ReuseEntryForTodayUseCase>(relaxed = true)
    private val filterEntriesBySearchFieldUseCase = mockk<FilterEntriesBySearchFieldUseCase>()

    private lateinit var viewModel: EntryListDisplayingViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val sampleEntry =
        SugarEntry(
            id = 1,
            currentTimestamp = 1000L,
            date = "2023-10-27",
            category = "Apple",
            entryType = GramCountMode.PerPiece,
            gram = 100.0,
            quantity = 1.0,
            gramTotal = 100.0,
        )

    private val sampleEntryGroup =
        EntryGroup(
            date = "2023-10-27",
            dayDisplayFormat = "Today",
            entryList = listOf(sampleEntry),
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        every { getEntryGroupPerDayUseCase(any()) } returns flowOf(listOf(sampleEntryGroup))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Dispatchers::class)
    }

    private fun initViewModel() {
        viewModel =
            EntryListDisplayingViewModel(
                getEntryGroupPerDayUseCase,
                deleteEntryUseCase,
                editDatabaseEntryUseCase,
                reuseEntryForTodayUseCase,
                filterEntriesBySearchFieldUseCase,
            )
    }

    @Test
    fun `initialization loads data into states`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            val state = viewModel.entryListDisplayingStates.value
            assertTrue(state is EntryListDisplayingStates.Success)
            val data = (state as EntryListDisplayingStates.Success).data
            assertEquals(listOf(sampleEntryGroup), data.entriesGroupedPerDayCounter)
            assertEquals(listOf(sampleEntryGroup), data.entriesGroupedPerDayHistory)
        }

    @Test
    fun `OpenCardDetails updates state with selected entry`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.OpenCardDetails(sampleEntry))

            val state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertTrue(state.data.showCardItemBottomSheet)
            assertEquals(sampleEntry, state.data.entryInCardItem)
        }

    @Test
    fun `LoadEntryDataIntoCardDetails updates state with entry values`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.LoadEntryDataIntoCardDetails(sampleEntry))

            val state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals("Apple", state.data.valueCategory)
            assertEquals("100.0", state.data.valueGram)
            assertEquals("1.0", state.data.valueQuantity)
        }

    @Test
    fun `EditCategory updates state`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.EditCategory("Banana"))

            val state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals("Banana", state.data.valueCategory)
        }

    @Test
    fun `DismissCardDetails updates state`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.DismissCardDetails)

            val state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals(false, state.data.showCardItemBottomSheet)
        }

    @Test
    fun `DeleteEntry calls deleteEntryUseCase`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.DeleteEntry(1))
            advanceUntilIdle()

            verify { deleteEntryUseCase(1) }
        }

    @Test
    fun `ChangeSearchTextFieldText updates state`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.ChangeSearchTextFieldText("Search query"))

            val state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals("Search query", state.data.searchFieldText)
        }

    @Test
    fun `FilterEntryListInHistory calls filterUseCase and updates state`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.ChangeSearchTextFieldText("Apple"))
            every { filterEntriesBySearchFieldUseCase("Apple", any()) } returns listOf(sampleEntryGroup)

            viewModel.onAction(EntryListDisplayingIntents.FilterEntryListInHistory)

            val state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals(listOf(sampleEntryGroup), state.data.entriesGroupedPerDayHistory)
            verify { filterEntriesBySearchFieldUseCase("Apple", any()) }
        }

    @Test
    fun `CloseSearchFieldAndClearText resets search state`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.ChangeSearchTextFieldText("Apple"))
            viewModel.onAction(EntryListDisplayingIntents.CloseSearchFieldAndClearText)

            val state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals(false, state.data.searchFieldShown)
            assertEquals("", state.data.searchFieldText)
            assertEquals(listOf(sampleEntryGroup), state.data.entriesGroupedPerDayHistory)
        }

    @Test
    fun `ReuseEntryForToday calls reuseEntryForTodayUseCase`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.OpenCardDetails(sampleEntry))
            viewModel.onAction(EntryListDisplayingIntents.ReuseEntryForToday)
            advanceUntilIdle()

            verify { reuseEntryForTodayUseCase(sampleEntry) }
        }

    @Test
    fun `EditGram updates state`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.EditGram("25.5"))

            val state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals("25.5", state.data.valueGram)
        }

    @Test
    fun `EditQuantity updates state`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.EditQuantity("2.0"))

            val state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals("2.0", state.data.valueQuantity)
        }

    @Test
    fun `ShowDeleteEntryConfirmation updates state`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            viewModel.onAction(EntryListDisplayingIntents.ShowDeleteEntryConfirmation(true))

            val state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals(true, state.data.entryDeletionConfirmationDialogShown)
        }

    @Test
    fun `ChangeSearchFieldShown toggles searchFieldShown state`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            // Toggle to true
            viewModel.onAction(EntryListDisplayingIntents.ChangeSearchFieldShown)
            var state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals(true, state.data.searchFieldShown)

            // Toggle back to false
            viewModel.onAction(EntryListDisplayingIntents.ChangeSearchFieldShown)
            state = viewModel.entryListDisplayingStates.value as EntryListDisplayingStates.Success
            assertEquals(false, state.data.searchFieldShown)
        }

    @Test
    fun `EditEntryInDB calls editDatabaseEntryUseCase with correct parameters`() =
        runTest {
            initViewModel()
            advanceUntilIdle()

            // Setup state by loading an entry and then editing fields
            viewModel.onAction(EntryListDisplayingIntents.OpenCardDetails(sampleEntry))
            viewModel.onAction(EntryListDisplayingIntents.LoadEntryDataIntoCardDetails(sampleEntry))
            viewModel.onAction(EntryListDisplayingIntents.EditCategory("New Category"))
            viewModel.onAction(EntryListDisplayingIntents.EditGram("50.0"))
            viewModel.onAction(EntryListDisplayingIntents.EditQuantity("3.0"))

            viewModel.onAction(EntryListDisplayingIntents.EditEntryInDB)
            advanceUntilIdle()

            verify {
                editDatabaseEntryUseCase(
                    sugarEntryID = sampleEntry.id,
                    sugarEntryType = sampleEntry.entryType,
                    newCategory = "New Category",
                    newGram = 50.0,
                    oldCategory = sampleEntry.category,
                    newQuantity = 3.0,
                )
            }
        }

    @Test
    fun `groupEntriesPerDayCounter handles error and updates state`() =
        runTest {
            val errorMessage = "Network Error"
            every { getEntryGroupPerDayUseCase(any()) } returns
                flow {
                    throw IllegalStateException(errorMessage)
                }

            initViewModel()
            advanceUntilIdle()

            val state = viewModel.entryListDisplayingStates.value
            assertTrue(state is EntryListDisplayingStates.Error)
            assertEquals(errorMessage, (state as EntryListDisplayingStates.Error).message)
        }
}
