package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import io.mockk.clearAllMocks
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

class ReuseEntryForTodayUseCaseTest {
    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockDao: DaoAppDatabase
    private lateinit var useCase: ReuseEntryForTodayUseCase
    private val sugarEntrySlot = slot<SugarEntry>()

    @Before
    fun setUp() {
        mockDatabase = mockk()
        mockDao = mockk()
        every { mockDatabase.appDao() } returns mockDao
        // Tell MockK to "just run" the insert function and capture its argument
        coJustRun { mockDao.insertSugarEntry(capture(sugarEntrySlot)) }
        useCase = ReuseEntryForTodayUseCase(mockDatabase)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `invoke preserves PerHundred entryType when reusing entry`() =
        runTest {
            // Arrange
            val originalEntry =
                SugarEntry(
                    id = 1,
                    currentTimestamp = 12345L,
                    date = "2025-01-01",
                    category = "Cereal",
                    entryType = GramCountMode.PerHundred,
                    gramPerHundred = 25.0,
                    gramPerPiece = 0.0,
                    quantity = 50.0,
                    amount = 0.0,
                    gramTotal = 12.5,
                )

            // Act
            useCase.invoke(originalEntry)

            // Assert
            // Verify that the insert function was called exactly once
            coVerify(exactly = 1) { mockDao.insertSugarEntry(any()) }

            // Assert that the entryType of the captured SugarEntry is correct
            val capturedEntry = sugarEntrySlot.captured
            assertEquals(GramCountMode.PerHundred, capturedEntry.entryType)

            // Assert that other important data was preserved
            assertEquals(originalEntry.category, capturedEntry.category)
            assertEquals(originalEntry.gramPerHundred!!, capturedEntry.gramPerHundred!!, 0.0)
        }

    @Test
    fun `invoke preserves PerPiece entryType when reusing entry`() =
        runTest {
            // Arrange
            val originalEntry =
                SugarEntry(
                    id = 2,
                    currentTimestamp = 67890L,
                    date = "2025-02-02",
                    category = "Cookie",
                    entryType = GramCountMode.PerPiece, // This triggers the 'else' branch
                    gramPerHundred = 0.0,
                    gramPerPiece = 15.0,
                    quantity = 1.0,
                    amount = 2.0,
                    gramTotal = 30.0,
                )

            // Act
            useCase.invoke(originalEntry)

            // Assert
            coVerify(exactly = 1) { mockDao.insertSugarEntry(any()) }

            // Assert that the entryType of the captured SugarEntry is correct
            val capturedEntry = sugarEntrySlot.captured
            assertEquals(GramCountMode.PerPiece, capturedEntry.entryType)

            // Assert that other important data was preserved
            assertEquals(originalEntry.category, capturedEntry.category)
            assertEquals(originalEntry.gramPerPiece!!, capturedEntry.gramPerPiece!!, 0.0)
        }
}
