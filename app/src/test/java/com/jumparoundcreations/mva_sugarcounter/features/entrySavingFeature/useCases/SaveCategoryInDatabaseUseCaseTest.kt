package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import android.util.Log
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.Category
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SaveCategoryInDatabaseUseCaseTest {

    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockDao: DaoAppDatabase
    private lateinit var useCase: SaveCategoryInDatabaseUseCase

    @Before
    fun setUp() {
        mockDatabase = mockk()
        mockDao = mockk()
        every { mockDatabase.appDao() } returns mockDao

        // `coJustRun` is used for suspend functions that return Unit.
        // It tells MockK to "just run" the function without doing anything.
        coJustRun { mockDao.insertCategory(any()) }
        coJustRun { mockDao.updateCategory(any()) }

        // Instantiate the class under test
        useCase = SaveCategoryInDatabaseUseCase(mockDatabase)
    }

    @Test
    fun `invoke calls insertCategory when category is new`() = runTest {
        // Arrange
        val categoryName = "New Cereal"
        val barcode = "123456789"
        val state = EntrySavingStates(
            categoryInField = categoryName,
            barcodeNumber = barcode,
            // The list of existing categories does NOT contain the new one
            categoryListInDropdown = listOf("Yogurt", "Bread")
        )

        // Act
        useCase(state)

        // --- Assert ---
        // Verify that insertCategory was called exactly once with the correct data
        coVerify(exactly = 1) {
            mockDao.insertCategory(
                match { it.category == categoryName && it.barcodeNumber == barcode }
            )
        }
        // Verify that updateCategory was never called
        coVerify(exactly = 0) { mockDao.updateCategory(any()) }
    }

    @Test
    fun `invoke calls updateCategory when category exists and barcode is provided`() = runTest {
        // Arrange
        val existingCategoryName = "Yogurt"
        val newBarcode = "987654321"
        val state = EntrySavingStates(
            categoryInField = existingCategoryName,
            barcodeNumber = newBarcode,
            // The list of existing categories DOES contain the category
            categoryListInDropdown = listOf("Yogurt", "Bread")
        )

        // The category object that the DAO will "find" in the database
        val existingCategoryRow = Category(category = existingCategoryName, barcodeNumber = "")
        coEvery { mockDao.getCategoryByCategoryName(existingCategoryName) } returns existingCategoryRow

        // Act
        useCase(state)

        // Assert
        // Verify that updateCategory was called exactly once
        coVerify(exactly = 1) {
            // Check that the category being updated has the new barcode assigned
            mockDao.updateCategory(
                match { it.category == existingCategoryName && it.barcodeNumber == newBarcode }
            )
        }
        // Verify that insertCategory was never called
        coVerify(exactly = 0) { mockDao.insertCategory(any()) }
    }

    @Test
    fun `invoke does nothing when category exists and barcode is empty`() = runTest {

        // Arrange
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        val existingCategoryName = "Yogurt"
        val state = EntrySavingStates(
            categoryInField = existingCategoryName,
            barcodeNumber = "", // Barcode is empty
            categoryListInDropdown = listOf("Yogurt", "Bread")
        )

        // --- Act ---
        useCase(state)

        // Assert
        // Verify that no write operations (insert or update) were performed on the DAO
        coVerify(exactly = 0) { mockDao.insertCategory(any()) }
        coVerify(exactly = 0) { mockDao.updateCategory(any()) }
        // Verify that getCategory was also not called, as the logic branch is exited early
        coVerify(exactly = 0) { mockDao.getCategoryByCategoryName(any()) }
    }

    @Test
    fun `invoke trims whitespace from category and barcode`() = runTest {
        // Arrange
        val categoryWithWhitespace = " New Cereal "
        val barcodeWithWhitespace = " 12345 "
        val state = EntrySavingStates(
            categoryInField = categoryWithWhitespace,
            barcodeNumber = barcodeWithWhitespace,
            categoryListInDropdown = emptyList() // New category
        )

        // Act
        useCase(state)

        // Assert
        // Verify that insertCategory was called with trimmed values
        coVerify(exactly = 1) {
            mockDao.insertCategory(
                match { it.category == "New Cereal" && it.barcodeNumber == "12345" }
            )
        }
    }
}