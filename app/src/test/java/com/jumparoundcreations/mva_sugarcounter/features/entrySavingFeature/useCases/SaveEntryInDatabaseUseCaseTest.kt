package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SaveEntryInDatabaseUseCaseTest {
    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockDao: DaoAppDatabase
    private lateinit var useCase: SaveEntryInDatabaseUseCase

    @Before
    fun setUp() {
        mockDatabase = mockk()
        mockDao = mockk()

        every { mockDatabase.appDao() } returns mockDao

        // `coJustRun` is for suspend functions. insertSugarEntry is suspend.
        coJustRun { mockDao.insertSugarEntry(any()) }

        useCase = SaveEntryInDatabaseUseCase(mockDatabase)
    }

    @Test
    fun `invoke calculates gramTotal correctly for PerPiece mode`() =
        runTest {
            // Arrange
            val state =
                EntrySavingStates(
                    dateOfEntryEpochSec = 1765566000L, // A specific timestamp (Dec 12, 2025 11:00:00 AM UTC)
                    categoryInField = "Cookie",
                    gramCountMode = GramCountMode.PerPiece, // The mode being tested
                    entryFieldGramPerPiece = "15.5",
                    entryFieldAmount = "2",
                )
            // Expected total = 15.5 * 2 = 31.0
            val expectedGramTotal = 31.0

            // Use a CapturingSlot to grab the SugarEntry passed to the DAO
            val slot = slot<SugarEntry>()
            coEvery { mockDao.insertSugarEntry(capture(slot)) } coAnswers { }

            // Act
            useCase(state)

            // Assert
            // Verify that the insert method was called exactly once
            coVerify(exactly = 1) { mockDao.insertSugarEntry(any()) }

            // Check the values of the captured SugarEntry
            val capturedEntry = slot.captured
            assertEquals("Cookie", capturedEntry.category)
            assertEquals(GramCountMode.PerPiece, capturedEntry.entryType)
            assertEquals(15.5, capturedEntry.gramPerPiece!!, 0.0)
            assertEquals(2.0, capturedEntry.amount!!, 0.0)
            assertEquals(expectedGramTotal, capturedEntry.gramTotal!!, 0.0)
        }

    @Test
    fun `invoke calculates gramTotal correctly for PerHundred mode`() =
        runTest {
            // Arrange
            val state =
                EntrySavingStates(
                    dateOfEntryEpochSec = 1765566000L,
                    categoryInField = "Cereal",
                    gramCountMode = GramCountMode.PerHundred, // The mode being tested
                    entryFieldGramPerHundred = "25", // Sugar per 100g
                    entryFieldQuantity = "50", // Grams of cereal eaten
                )
            // Expected total = (25 / 100) * 50 = 12.5
            val expectedGramTotal = 12.5

            val slot = slot<SugarEntry>()
            coEvery { mockDao.insertSugarEntry(capture(slot)) } coAnswers { }

            // --- Act ---
            useCase(state)

            // --- Assert ---
            coVerify(exactly = 1) { mockDao.insertSugarEntry(any()) }

            val capturedEntry = slot.captured
            assertEquals("Cereal", capturedEntry.category)
            assertEquals(GramCountMode.PerHundred, capturedEntry.entryType)
            assertEquals(25.0, capturedEntry.gramPerHundred!!, 0.0)
            assertEquals(50.0, capturedEntry.quantity!!, 0.0)
            assertEquals(expectedGramTotal, capturedEntry.gramTotal!!, 0.0)
        }
}
