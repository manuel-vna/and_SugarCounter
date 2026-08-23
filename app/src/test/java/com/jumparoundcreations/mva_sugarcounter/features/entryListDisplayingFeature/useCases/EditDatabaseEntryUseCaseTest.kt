package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EditDatabaseEntryUseCaseTest {
    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockDao: DaoAppDatabase
    private lateinit var useCase: EditDatabaseEntryUseCase

    // A slot to capture the arguments passed to the DAO methods
    private val idSlot = slot<Int>()
    private val gramPerHundredSlot = slot<Double>()
    private val gramPerPieceSlot = slot<Double>()
    private val quantitySlot = slot<Double>()
    private val amountSlot = slot<Double>()
    private val gramTotalSlot = slot<Double>()

    @Before
    fun setUp() {
        mockDatabase = mockk()
        mockDao = mockk()
        every { mockDatabase.appDao() } returns mockDao

        // `coJustRun` tells MockK to "just run" these void functions without doing anything.
        // We capture the arguments passed to updateSugarEntry to verify them later.
        coJustRun {
            mockDao.updateSugarEntry(
                id = capture(idSlot),
                gramPerHundred = capture(gramPerHundredSlot),
                gramPerPiece = capture(gramPerPieceSlot),
                quantity = capture(quantitySlot),
                amount = capture(amountSlot),
                gramTotal = capture(gramTotalSlot),
            )
        }
        coJustRun {
            mockDao.updateEntrySugarCategoryOfLastXDays(
                oldCategory = any(),
                newCategory = any(),
                startPoint = any(),
                endPoint = any(),
            )
        }
        coJustRun {
            mockDao.updateCategoryOnEdit(
                oldCategory = any(),
                newCategory = any(),
            )
        }

        // Instantiate the class under test
        useCase = EditDatabaseEntryUseCase(mockDatabase)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `invoke calculates gramTotal correctly for PerHundred mode`() =
        runTest {
            // Arrange
            val sugarEntryID = 1
            val currentEntry =
                SugarEntry(
                    id = sugarEntryID,
                    currentTimestamp = 0L,
                    date = "",
                    category = "Old",
                    entryType = GramCountMode.PerHundred,
                    gramPerHundred = 0.0,
                    gramPerPiece = 0.0,
                    quantity = 0.0,
                    amount = 0.0,
                    gramTotal = 0.0,
                )
            coEvery { mockDao.getSugarEntryById(sugarEntryID) } returns currentEntry

            val newGramPerHundred = 25.0
            val newQuantity = 50.0
            val expectedGramTotal = 12.5

            // Act
            useCase.invoke(
                sugarEntryID = sugarEntryID,
                sugarEntryType = GramCountMode.PerHundred,
                oldCategory = "Old",
                newCategory = "New",
                newGramPerHundred = newGramPerHundred,
                newGramPerPiece = 0.0,
                newQuantity = newQuantity,
                newAmount = 0.0,
            )

            // Assert
            // Verify that the updateSugarEntry function was called exactly once
            coVerify(exactly = 1) {
                mockDao.updateSugarEntry(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            }

            // Assert that the captured gramTotal value matches our expected calculation
            assertEquals(expectedGramTotal, gramTotalSlot.captured!!, 0.001)

            // Assert other captured values are correct
            assertEquals(sugarEntryID, idSlot.captured)
            assertEquals(newGramPerHundred, gramPerHundredSlot.captured!!, 0.0)
            assertEquals(newQuantity, quantitySlot.captured!!, 0.0)
        }

    @Test
    fun `invoke calculates gramTotal correctly for PerPiece mode`() =
        runTest {
            // Arrange
            val sugarEntryID = 2
            val currentEntry =
                SugarEntry(
                    id = sugarEntryID,
                    currentTimestamp = 0L,
                    date = "",
                    category = "Old",
                    entryType = GramCountMode.PerPiece,
                    gramPerHundred = 0.0,
                    gramPerPiece = 0.0,
                    quantity = 0.0,
                    amount = 0.0,
                    gramTotal = 0.0,
                )
            coEvery { mockDao.getSugarEntryById(sugarEntryID) } returns currentEntry

            val newGramPerPiece = 15.0
            val newAmount = 3.0
            val expectedGramTotal = 45.0

            // Act
            useCase.invoke(
                sugarEntryID = sugarEntryID,
                sugarEntryType = GramCountMode.PerPiece,
                oldCategory = "Old",
                newCategory = "New",
                newGramPerHundred = 0.0,
                newGramPerPiece = newGramPerPiece,
                newQuantity = 0.0,
                newAmount = newAmount,
            )

            // Assert
            // Verify that the updateSugarEntry function was called exactly once
            coVerify(exactly = 1) {
                mockDao.updateSugarEntry(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            }

            // Assert that the captured gramTotal value is correct for the PerPiece calculation
            assertEquals(expectedGramTotal, gramTotalSlot.captured!!, 0.001)

            // Assert other captured values are correct
            assertEquals(sugarEntryID, idSlot.captured)
            assertEquals(newGramPerPiece, gramPerPieceSlot.captured!!, 0.0)
            assertEquals(newAmount, amountSlot.captured!!, 0.0)
        }
}
