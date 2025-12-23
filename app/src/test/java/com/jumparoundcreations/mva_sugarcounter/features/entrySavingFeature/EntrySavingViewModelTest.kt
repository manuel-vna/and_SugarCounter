package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.Category
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckThresholdResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckUserInputResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GetEntryByCategoryResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.CheckDailyGramThresholdUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.CheckForDefaultSavingValuesUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.CheckUserInputUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.DisplayAllCategoriesUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.GetEntryByCategoryUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.SaveCategoryInDatabaseUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.SaveEntryInDatabaseUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases.ScanBarcodeUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.assertEquals

class EntrySavingViewModelTest {

    private val mockScanBarcodeUseCase = mockk<ScanBarcodeUseCase>()
    private val getEntryByCategoryUseCase = mockk<GetEntryByCategoryUseCase>()
    private val saveEntryInDatabaseUseCase = mockk<SaveEntryInDatabaseUseCase>()
    private val saveCategoryInDatabaseUseCase = mockk<SaveCategoryInDatabaseUseCase>()
    private val checkUserInputUseCase = mockk<CheckUserInputUseCase>()
    private val checkForDefaultSavingValuesUseCase = mockk<CheckForDefaultSavingValuesUseCase>()
    private val displayAllCategoriesUseCase = mockk<DisplayAllCategoriesUseCase>(relaxed = true)
    private val checkDailyGramThresholdUseCase = mockk<CheckDailyGramThresholdUseCase>()


    private val category = mockk<Category>()
    private lateinit var viewModel: EntrySavingViewModel


    @Before
    fun setup() {
        viewModel = EntrySavingViewModel(
            mockScanBarcodeUseCase,
            getEntryByCategoryUseCase,
            saveEntryInDatabaseUseCase,
            saveCategoryInDatabaseUseCase,
            checkUserInputUseCase,
            checkForDefaultSavingValuesUseCase,
            displayAllCategoriesUseCase,
            checkDailyGramThresholdUseCase
        )

        every { displayAllCategoriesUseCase() } returns flowOf(listOf(category))
        every { saveEntryInDatabaseUseCase(any()) } returns Unit
        every { saveCategoryInDatabaseUseCase(any()) } returns Unit
    }


    @Test
    fun `test EntrySavingIntents OpenAndCloseDatePicker and ChangeSelectedDate`() = runTest {

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

    @Test
    fun `test EntrySavingIntents EditOfCategoryField`() = runTest {
        val categoryInField = "Chocolate"
        val categoryDropdownExpanded = true

        viewModel.onAction(
            EntrySavingIntents.EditOfCategoryField(
                categoryInField = categoryInField,
                categoryDropdownExpanded = categoryDropdownExpanded
            )
        )
        assertEquals(
            expected = categoryInField,
            actual = viewModel.entrySavingStates.value.categoryInField
        )
        assertEquals(
            expected = categoryDropdownExpanded,
            actual = viewModel.entrySavingStates.value.categoryDropdownExpanded
        )

        viewModel.onAction(
            EntrySavingIntents.ExpandOrCollapseCategoryDropdown(
                categoryDropdownExpanded = categoryDropdownExpanded
            )
        )
        assertEquals(
            expected = categoryDropdownExpanded,
            actual = viewModel.entrySavingStates.value.categoryDropdownExpanded
        )
    }

    @Test
    fun `test EntrySavingIntents EditOfCategoryWithinDropdown with EntryFound`() = runTest {
        // Arrange
        val categoryInDropdown = "Chocolate"
        val gram = 8.0
        val quantity = 2.0
        val gramTotal = gram * quantity
        val foundEntry = GetEntryByCategoryResult.EntryFound(
            entry = SugarEntry(
                id = 0,
                currentTimestamp = 123456789L,
                date = "2025-12-15",
                category = "Chocolate",
                entryType = GramCountMode.PerPiece,
                gram = gram,
                quantity = quantity,
                gramTotal = gramTotal
            )
        )
        coEvery { getEntryByCategoryUseCase(categoryInDropdown) } returns foundEntry

        // Act
        viewModel.onAction(
            EntrySavingIntents.EditOfCategoryWithinDropdown(
                categoryInDropdown = categoryInDropdown,
                categoryDropdownExpanded = false
            )
        )

        // Assert
        assertEquals(categoryInDropdown, viewModel.entrySavingStates.value.categoryInField)
        assertEquals(false, viewModel.entrySavingStates.value.categoryDropdownExpanded)
        assertEquals(gram, viewModel.entrySavingStates.value.entryFieldGram.toDouble())
        assertEquals(quantity, viewModel.entrySavingStates.value.entryFieldQuantity.toDouble())
    }

    @Test
    fun `test EntrySavingIntents EditOfCategoryWithinDropdown with NoEntryFound`() = runTest {

        //Arrange
        val categoryInDropdown = "Chocolate"
        val categoryDropdownExpanded = true
        coEvery { getEntryByCategoryUseCase(any()) } returns GetEntryByCategoryResult.NoEntryFound

        //Act
        viewModel.onAction(
            EntrySavingIntents.EditOfCategoryWithinDropdown(
                categoryInDropdown = categoryInDropdown,
                categoryDropdownExpanded = categoryDropdownExpanded
            )
        )

        //Assert
        assertEquals(
            expected = categoryInDropdown,
            actual = viewModel.entrySavingStates.value.categoryInField
        )
        assertEquals(
            expected = categoryDropdownExpanded,
            actual = viewModel.entrySavingStates.value.categoryDropdownExpanded
        )
        assertEquals(
            expected = "",
            actual = viewModel.entrySavingStates.value.entryFieldGram
        )
        assertEquals(
            expected = "",
            actual = viewModel.entrySavingStates.value.entryFieldQuantity
        )
    }

    @Test
    fun `test for adding a perPiece entry`() = runTest {
        // Arrange
        val perPieceTabIndex = 1
        val perPiece = GramCountMode.PerPiece
        val entryFieldGram = "45"
        val entryFieldQuantity = "25"

        //Act
        viewModel.onAction(
            EntrySavingIntents.ChangeGramCountModeTabIndex(
                tabIndex = perPieceTabIndex
            )
        )
        viewModel.onAction(
            EntrySavingIntents.ChangeGramCountMode(
                gramCountMode = perPiece
            )
        )
        viewModel.onAction(
            EntrySavingIntents.ChangeEntryFieldGram(
                entryFieldGram = entryFieldGram

            )
        )
        viewModel.onAction(
            EntrySavingIntents.ChangeEntryFieldQuantity(
                entryFieldQuantity = entryFieldQuantity

            )
        )

        // Assert

        assertEquals(
            expected = perPieceTabIndex,
            actual = viewModel.entrySavingStates.value.gramCountModeTabIndex
        )

        assertEquals(
            expected = perPiece,
            actual = viewModel.entrySavingStates.value.gramCountMode
        )
        assertEquals(
            expected = entryFieldGram,
            actual = viewModel.entrySavingStates.value.entryFieldGram
        )
        assertEquals(
            expected = entryFieldQuantity,
            actual = viewModel.entrySavingStates.value.entryFieldQuantity
        )
    }

    @Test
    fun `test EntrySavingIntents SaveSugarEntry with NoCategoryGiven and DismissNoCategoryDataEnteredAlert`() =
        runTest {
            //Arrange
            every { checkForDefaultSavingValuesUseCase(any()) } returns false
            every { checkUserInputUseCase(any()) } returns CheckUserInputResult.NoCategoryGiven

            //Act
            viewModel.onAction(
                action = EntrySavingIntents.SaveSugarEntry
            )

            //Assert
            assertEquals(
                expected = true,
                actual = viewModel.entrySavingStates.value.savingProcessMissingCategoryData
            )

            //Act
            viewModel.onAction(
                action = EntrySavingIntents.DismissNoCategoryDataEnteredAlert
            )

            //Arrange
            assertEquals(
                expected = false,
                actual = viewModel.entrySavingStates.value.savingProcessMissingCategoryData
            )
        }

    @Test
    fun `test EntrySavingIntents SaveSugarEntry with NoGramDataGivenButCategoryGiven and DismissNoSugarDataEnteredAlert`() =
        runTest {
            //Arrange
            every { checkForDefaultSavingValuesUseCase(any()) } returns true
            every { checkUserInputUseCase(any()) } returns
                    CheckUserInputResult.NoGramDataGivenButCategoryGiven

            //Act
            viewModel.onAction(
                action = EntrySavingIntents.SaveSugarEntry
            )

            //Assert
            assertEquals(
                expected = "",
                actual = viewModel.entrySavingStates.value.barcodeNumber
            )
            assertEquals(
                expected = true,
                actual = viewModel.entrySavingStates.value.savingProcessMissingSugarData
            )

            //Act
            viewModel.onAction(
                action = EntrySavingIntents.DismissNoSugarDataEnteredAlert
            )

            //Assert
            assertEquals(
                expected = false,
                actual = viewModel.entrySavingStates.value.savingProcessMissingSugarData
            )

        }

    @Ignore
    @Test
    fun `test EntrySavingIntents SaveSugarEntry with InputDataComplete`() = runTest {
        //Arrange
        every { checkForDefaultSavingValuesUseCase(any()) } returns false
        every { checkUserInputUseCase(any()) } returns
                CheckUserInputResult.InputDataComplete
        every { checkDailyGramThresholdUseCase(any()) } returns
                CheckThresholdResult.WithinDailyThresholdBoundaries

        //Act
        viewModel.onAction(
            action = EntrySavingIntents.SaveSugarEntry
        )

        //Assert
        assertEquals(
            expected = "",
            actual = viewModel.entrySavingStates.value.barcodeNumber
        )
        assertEquals(
            expected = CheckThresholdResult.WithinDailyThresholdBoundaries,
            actual = viewModel.entrySavingStates.value.savingProcessDailyGramThreshold
        )
    }

    @Ignore
    @Test
    fun `test EntrySavingIntents SaveSugarEntry with InputDataComplete and UserThresholdReaction`() =
        runTest {
            //Arrange
            every { checkForDefaultSavingValuesUseCase(any()) } returns false
            every { checkUserInputUseCase(any()) } returns
                    CheckUserInputResult.InputDataComplete
            every { checkDailyGramThresholdUseCase(any()) } returns
                    CheckThresholdResult.DailyThresholdBreached


            //Act
            viewModel.onAction(
                action = EntrySavingIntents.SaveSugarEntry
            )

            //Assert
            assertEquals(
                expected = "",
                actual = viewModel.entrySavingStates.value.barcodeNumber
            )
            assertEquals(
                expected = CheckThresholdResult.DailyThresholdBreached,
                actual = viewModel.entrySavingStates.value.savingProcessDailyGramThreshold
            )

        }

    @Test
    fun `test EntrySavingIntents ClearInputFields`() = runTest {
        //Act
        viewModel.onAction(
            action = EntrySavingIntents.ClearInputFields

        )
        //Assert
        assertEquals(
            expected = "",
            actual = viewModel.entrySavingStates.value.categoryInField
        )
        assertEquals(
            expected = "",
            actual = viewModel.entrySavingStates.value.entryFieldGram
        )
        assertEquals(
            expected = "",
            actual = viewModel.entrySavingStates.value.entryFieldQuantity
        )
        assertEquals(
            expected = 0,
            actual = viewModel.entrySavingStates.value.gramCountModeTabIndex
        )


    }

}