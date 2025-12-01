package com.jumparoundcreations.sugarcounter.features.entrySavingFeature

import com.jumparoundcreations.sugarcounter.data.categoryData.Category
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GetEntryByCategoryResult
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.CheckForDefaultSavingValuesUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.CheckUserInputUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.DisplayAllCategoriesUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.GetEntryByCategoryUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.SaveCategoryInDatabaseUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.SaveEntryInDatabaseUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.ScanBarcodeUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
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
    private val displayAllCategoriesUseCase = mockk<DisplayAllCategoriesUseCase>()

    private val category = mockk<Category>()

    private val viewModel = EntrySavingViewModel(
        mockScanBarcodeUseCase,
        getEntryByCategoryUseCase,
        saveEntryInDatabaseUseCase,
        saveCategoryInDatabaseUseCase,
        checkUserInputUseCase,
        checkForDefaultSavingValuesUseCase,
        displayAllCategoriesUseCase
    )

    @Before
    fun setup() {
        coEvery { displayAllCategoriesUseCase() } returns flowOf(listOf(category))
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
    fun `test EntrySavingIntents EditOfCategoryField and CategoryDropdownExpanded`() = runTest {
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
}