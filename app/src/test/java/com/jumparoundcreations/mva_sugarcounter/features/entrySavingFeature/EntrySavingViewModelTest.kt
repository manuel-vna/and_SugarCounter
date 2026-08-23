package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature

import android.content.Context
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.Category
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckThresholdResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckUserInputResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GetEntryByCategoryResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.CheckDailyGramThresholdUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.CheckForDefaultSavingValuesUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.CheckUserInputUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.DisplayAllCategoriesUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.GetEntryByCategoryUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.GetEntryFromApiUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.SaveCategoryInDatabaseUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.SaveEntryInDatabaseUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.ScanBarcodeUseCase
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class EntrySavingViewModelTest {
    private val mockContext = mockk<Context>(relaxed = true)
    private val mockScanBarcodeUseCase = mockk<ScanBarcodeUseCase>()
    private val getEntryByCategoryUseCase = mockk<GetEntryByCategoryUseCase>()
    private val saveEntryInDatabaseUseCase = mockk<SaveEntryInDatabaseUseCase>()

    private val getEntryFromApiUseCase = mockk<GetEntryFromApiUseCase>()
    private val saveCategoryInDatabaseUseCase = mockk<SaveCategoryInDatabaseUseCase>()
    private val checkUserInputUseCase = mockk<CheckUserInputUseCase>()
    private val checkForDefaultSavingValuesUseCase = mockk<CheckForDefaultSavingValuesUseCase>()
    private val displayAllCategoriesUseCase = mockk<DisplayAllCategoriesUseCase>(relaxed = true)
    private val checkDailyGramThresholdUseCase = mockk<CheckDailyGramThresholdUseCase>()

    private val category = Category(category = "Apple", barcodeNumber = "123")
    private val mockDatabase = mockk<AppDatabase>(relaxed = true)
    private lateinit var viewModel: EntrySavingViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher

        startKoin {
            modules(
                module {
                    single { mockDatabase }
                },
            )
        }

        viewModel =
            EntrySavingViewModel(
                mockContext,
                mockScanBarcodeUseCase,
                getEntryByCategoryUseCase,
                getEntryFromApiUseCase,
                saveEntryInDatabaseUseCase,
                saveCategoryInDatabaseUseCase,
                checkUserInputUseCase,
                checkForDefaultSavingValuesUseCase,
                displayAllCategoriesUseCase,
                checkDailyGramThresholdUseCase,
            )

        every { displayAllCategoriesUseCase() } returns flowOf(listOf(category))
        coJustRun { saveEntryInDatabaseUseCase(any()) }
        coJustRun { saveCategoryInDatabaseUseCase(any()) }
    }

    @After
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain()
        unmockkStatic(Dispatchers::class)
    }

    @Test
    fun testEntrySavingIntentsOpenAndCloseDatePickerandChangeSelectedDate() =
        runTest {
            viewModel.onAction(EntrySavingIntents.OpenAndCloseDatePicker)
            // advanceUntilIdle() here acts as a "synchronization point" that guarantees the ViewModel
            // has finished processing the intent before the outcome is verified with e.g. assertEquals()
            assertEquals(true, viewModel.entrySavingStates.value.datePickerShown)

            viewModel.onAction(EntrySavingIntents.OpenAndCloseDatePicker)
            advanceUntilIdle()
            assertEquals(false, viewModel.entrySavingStates.value.datePickerShown)

            // Arrange
            val newDate = 123456789L
            // Test
            viewModel.onAction(EntrySavingIntents.ChangeSelectedDate(newDate))
            advanceUntilIdle()
            assertEquals(
                newDate,
                viewModel.entrySavingStates.value.dateOfEntryEpochSec,
            )
        }

    @Test
    fun testEntrySavingIntentsEditOfCategoryField() =
        runTest {
            val categoryInField = "Chocolate"
            val categoryDropdownExpanded = true

            viewModel.onAction(
                EntrySavingIntents.EditOfCategoryField(
                    categoryInField = categoryInField,
                    categoryDropdownExpanded = categoryDropdownExpanded,
                ),
            )
            advanceUntilIdle()
            assertEquals(
                expected = categoryInField,
                actual = viewModel.entrySavingStates.value.categoryInField,
            )
            assertEquals(
                expected = categoryDropdownExpanded,
                actual = viewModel.entrySavingStates.value.categoryDropdownExpanded,
            )

            viewModel.onAction(
                EntrySavingIntents.ExpandOrCollapseCategoryDropdown(
                    categoryDropdownExpanded = categoryDropdownExpanded,
                ),
            )
            advanceUntilIdle()
            assertEquals(
                expected = categoryDropdownExpanded,
                actual = viewModel.entrySavingStates.value.categoryDropdownExpanded,
            )
        }

    @Test
    fun testEntrySavingIntentsEditOfCategoryWithinDropdownwithEntryFound() =
        runTest {
            // Arrange
            val categoryInDropdown = "Chocolate"
            val gramPerPiece = 8.0
            val amount = 2.0
            val gramTotal = gramPerPiece * amount
            val foundEntry =
                GetEntryByCategoryResult.EntryFound(
                    entry =
                        SugarEntry(
                            id = 0,
                            currentTimestamp = 123456789L,
                            date = "2025-12-15",
                            category = "Chocolate",
                            entryType = GramCountMode.PerPiece,
                            gramPerHundred = 0.0,
                            gramPerPiece = gramPerPiece,
                            quantity = 1.0,
                            amount = amount,
                            gramTotal = gramTotal,
                        ),
                )
            coEvery { getEntryByCategoryUseCase(categoryInDropdown) } returns foundEntry

            // Act
            viewModel.onAction(
                EntrySavingIntents.EditOfCategoryWithinDropdown(
                    categoryInDropdown = categoryInDropdown,
                    categoryDropdownExpanded = false,
                ),
            )
            advanceUntilIdle()

            // Assert
            assertEquals(categoryInDropdown, viewModel.entrySavingStates.value.categoryInField)
            assertEquals(false, viewModel.entrySavingStates.value.categoryDropdownExpanded)
            assertEquals(
                gramPerPiece,
                viewModel.entrySavingStates.value.entryFieldGramPerPiece
                    .toDouble(),
            )
            assertEquals(
                amount,
                viewModel.entrySavingStates.value.entryFieldAmount
                    .toDouble(),
            )
        }

    @Test
    fun testEntrySavingIntentsEditOfCategoryWithinDropdownwithNoEntryFound() =
        runTest {
            // Arrange
            val categoryInDropdown = "Chocolate"
            val categoryDropdownExpanded = true
            coEvery { getEntryByCategoryUseCase(any()) } returns GetEntryByCategoryResult.NoEntryFound

            // Act
            viewModel.onAction(
                EntrySavingIntents.EditOfCategoryWithinDropdown(
                    categoryInDropdown = categoryInDropdown,
                    categoryDropdownExpanded = categoryDropdownExpanded,
                ),
            )
            advanceUntilIdle()

            // Assert
            assertEquals(
                expected = categoryInDropdown,
                actual = viewModel.entrySavingStates.value.categoryInField,
            )
            assertEquals(
                expected = categoryDropdownExpanded,
                actual = viewModel.entrySavingStates.value.categoryDropdownExpanded,
            )
            assertEquals(
                expected = "",
                actual = viewModel.entrySavingStates.value.entryFieldGramPerHundred,
            )
            assertEquals(
                expected = "",
                actual = viewModel.entrySavingStates.value.entryFieldQuantity,
            )
        }

    @Test
    fun testforaddingaperPieceentry() =
        runTest {
            // Arrange
            val perPieceTabIndex = 1
            val perPiece = GramCountMode.PerPiece
            val entryFieldGram = "45"
            val entryFieldAmount = "25"

            // Act
            viewModel.onAction(
                EntrySavingIntents.ChangeGramCountModeTabIndex(
                    tabIndex = perPieceTabIndex,
                ),
            )
            viewModel.onAction(
                EntrySavingIntents.ChangeGramCountMode(
                    gramCountMode = perPiece,
                ),
            )
            viewModel.onAction(
                EntrySavingIntents.ChangeEntryFieldGramPerPiece(
                    entryFieldGramPerPiece = entryFieldGram,
                ),
            )
            viewModel.onAction(
                EntrySavingIntents.ChangeEntryFieldAmount(
                    entryFieldAmount = entryFieldAmount,
                ),
            )
            advanceUntilIdle()

            // Assert

            assertEquals(
                expected = perPieceTabIndex,
                actual = viewModel.entrySavingStates.value.gramCountModeTabIndex,
            )

            assertEquals(
                expected = perPiece,
                actual = viewModel.entrySavingStates.value.gramCountMode,
            )
            assertEquals(
                expected = entryFieldGram,
                actual = viewModel.entrySavingStates.value.entryFieldGramPerPiece,
            )
            assertEquals(
                expected = entryFieldAmount,
                actual = viewModel.entrySavingStates.value.entryFieldAmount,
            )
        }

    @Test
    fun testEntrySavingIntentsSaveSugarEntrywithNoCategoryGivenandDismissNoCategoryDataEnteredAlert() =
        runTest {
            // Arrange
            every { checkForDefaultSavingValuesUseCase(any()) } returns false
            every { checkUserInputUseCase(any()) } returns CheckUserInputResult.NoCategoryGiven

            // Act
            viewModel.onAction(
                action = EntrySavingIntents.SaveSugarEntry,
            )
            advanceUntilIdle()

            // Assert
            assertEquals(
                expected = true,
                actual = viewModel.entrySavingStates.value.savingProcessMissingCategoryData,
            )

            // Act
            viewModel.onAction(
                action = EntrySavingIntents.DismissNoCategoryDataEnteredAlert,
            )
            advanceUntilIdle()

            // Arrange
            assertEquals(
                expected = false,
                actual = viewModel.entrySavingStates.value.savingProcessMissingCategoryData,
            )
        }

    @Test
    fun testEntrySavingIntentsSaveSugarEntrywithNoGramDataGivenButCategoryGivenandDismissNoSugarDataEnteredAlert() =
        runTest {
            // Arrange
            every { checkForDefaultSavingValuesUseCase(any()) } returns true
            every { checkUserInputUseCase(any()) } returns
                CheckUserInputResult.NoGramDataGivenButCategoryGiven

            // Act
            viewModel.onAction(
                action = EntrySavingIntents.SaveSugarEntry,
            )
            advanceUntilIdle()

            // Assert
            assertEquals(
                expected = "",
                actual = viewModel.entrySavingStates.value.barcodeNumber,
            )
            assertEquals(
                expected = true,
                actual = viewModel.entrySavingStates.value.savingProcessMissingSugarData,
            )

            // Act
            viewModel.onAction(
                action = EntrySavingIntents.DismissNoSugarDataEnteredAlert,
            )
            advanceUntilIdle()

            // Assert
            assertEquals(
                expected = false,
                actual = viewModel.entrySavingStates.value.savingProcessMissingSugarData,
            )
        }

    @Test
    fun testEntrySavingIntentsSaveSugarEntrywithInputDataComplete() =
        runTest {
            // Arrange
            every { checkForDefaultSavingValuesUseCase(any()) } returns false
            every { checkUserInputUseCase(any()) } returns
                CheckUserInputResult.InputDataComplete
            coEvery { checkDailyGramThresholdUseCase(any()) } returns
                CheckThresholdResult.WithinDailyThresholdBoundaries

            // Act
            viewModel.onAction(
                action = EntrySavingIntents.SaveSugarEntry,
            )
            advanceUntilIdle()

            // Assert
            assertEquals(
                expected = "",
                actual = viewModel.entrySavingStates.value.barcodeNumber,
            )
            assertEquals(
                expected = CheckThresholdResult.WithinDailyThresholdBoundaries,
                actual = viewModel.entrySavingStates.value.savingProcessDailyGramThreshold,
            )
        }

    @Test
    fun testEntrySavingIntentsSaveSugarEntrywithInputDataCompleteandUserThresholdReaction() =
        runTest {
            // Arrange
            every { checkForDefaultSavingValuesUseCase(any()) } returns false
            every { checkUserInputUseCase(any()) } returns
                CheckUserInputResult.InputDataComplete
            coEvery { checkDailyGramThresholdUseCase(any()) } returns
                CheckThresholdResult.WithinDailyThresholdBoundaries

            // Act
            viewModel.onAction(
                action = EntrySavingIntents.SaveSugarEntry,
            )
            advanceUntilIdle()

            // Assert
            assertEquals(
                expected = "",
                actual = viewModel.entrySavingStates.value.barcodeNumber,
            )
            assertEquals(
                expected = CheckThresholdResult.WithinDailyThresholdBoundaries,
                actual = viewModel.entrySavingStates.value.savingProcessDailyGramThreshold,
            )
        }

    @Test
    fun testEntrySavingIntentsClearInputFields() =
        runTest {
            // Act
            viewModel.onAction(
                action = EntrySavingIntents.ClearInputFields,
            )
            advanceUntilIdle()
            // Assert
            assertEquals(
                expected = "",
                actual = viewModel.entrySavingStates.value.categoryInField,
            )
            assertEquals(
                expected = "",
                actual = viewModel.entrySavingStates.value.entryFieldGramPerHundred,
            )
            assertEquals(
                expected = "",
                actual = viewModel.entrySavingStates.value.entryFieldQuantity,
            )
            assertEquals(
                expected = 0,
                actual = viewModel.entrySavingStates.value.gramCountModeTabIndex,
            )
        }
}
