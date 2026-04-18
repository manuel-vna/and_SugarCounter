package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
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
    private val gramSlot = slot<Double>()
    private val quantitySlot = slot<Double>()
    private val gramTotalSlot = slot<Double>()

    @Before
    fun setUp() {
        mockDatabase = mockk()
        mockDao = mockk()
        every { mockDatabase.appDao() } returns mockDao

        // `justRun` tells MockK to "just run" these void functions without doing anything.
        // We capture the arguments passed to updateSugarEntry to verify them later.
        justRun {
            mockDao.updateSugarEntry(
                id = capture(idSlot),
                gram = capture(gramSlot),
                quantity = capture(quantitySlot),
                gramTotal = capture(gramTotalSlot)
            )
        }
        justRun {
            mockDao.updateEntrySugarCategoryOfLastXDays(
                oldCategory = any(),
                newCategory = any(),
                startPoint = any(),
                endPoint = any()
            )
        }
        justRun {
            mockDao.updateCategoryOnEdit(
                oldCategory = any(),
                newCategory = any()
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
    fun `invoke calculates gramTotal correctly for PerHundred mode`() {
        // Arrange
        val newGram = 25.0
        val newQuantity = 50.0
        val expectedGramTotal = (25.0 / 100.0) * 50.0 // = 12.5

        // Act
        useCase.invoke(
            sugarEntryID = 1,
            sugarEntryType = GramCountMode.PerHundred,
            oldCategory = "Old",
            newCategory = "New",
            newGram = newGram,
            newQuantity = newQuantity
        )

        // Assert
        // Verify that the updateSugarEntry function was called exactly once
        verify(exactly = 1) { mockDao.updateSugarEntry(any(), any(), any(), any()) }

        // Assert that the captured gramTotal value matches our expected calculation
        assertEquals(expectedGramTotal, gramTotalSlot.captured, 0.001)

        // Assert other captured values are correct
        assertEquals(1, idSlot.captured)
        assertEquals(newGram, gramSlot.captured, 0.0)
        assertEquals(newQuantity, quantitySlot.captured, 0.0)
    }

    @Test
    fun `invoke calculates gramTotal correctly for PerPiece mode`() {
        // Arrange
        val newGram = 15.0
        val newQuantity = 3.0
        val expectedGramTotal = 15.0 * 3.0 // = 45.0

        // Act
        useCase.invoke(
            sugarEntryID = 2,
            sugarEntryType = GramCountMode.PerPiece, // This triggers the 'else' branch
            oldCategory = "Old",
            newCategory = "New",
            newGram = newGram,
            newQuantity = newQuantity
        )

        // Assert
        // Verify that the updateSugarEntry function was called exactly once
        verify(exactly = 1) { mockDao.updateSugarEntry(any(), any(), any(), any()) }

        // Assert that the captured gramTotal value is correct for the PerPiece calculation
        assertEquals(expectedGramTotal, gramTotalSlot.captured, 0.001)

        // Assert other captured values are correct
        assertEquals(2, idSlot.captured)
        assertEquals(newGram, gramSlot.captured, 0.0)
        assertEquals(newQuantity, quantitySlot.captured, 0.0)
    }

}