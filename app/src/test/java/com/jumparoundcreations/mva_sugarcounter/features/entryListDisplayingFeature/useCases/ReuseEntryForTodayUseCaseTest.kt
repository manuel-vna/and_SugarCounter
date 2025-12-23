package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
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
        justRun { mockDao.insertSugarEntry(capture(sugarEntrySlot)) }
        useCase = ReuseEntryForTodayUseCase(mockDatabase)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `invoke preserves PerHundred entryType when reusing entry`() {
        // Arrange
        val originalEntry = SugarEntry(
            id = 1,
            currentTimestamp = 12345L,
            date = "2025-01-01",
            category = "Cereal",
            entryType = GramCountMode.PerHundred,
            gram = 25.0,
            quantity = 50.0,
            gramTotal = 12.5
        )

        // Act
        useCase.invoke(originalEntry)

        // Assert
        // Verify that the insert function was called exactly once
        verify(exactly = 1) { mockDao.insertSugarEntry(any()) }

        // Assert that the entryType of the captured SugarEntry is correct
        val capturedEntry = sugarEntrySlot.captured
        assertEquals(GramCountMode.PerHundred, capturedEntry.entryType)

        // Assert that other important data was preserved
        assertEquals(originalEntry.category, capturedEntry.category)
        assertEquals(originalEntry.gram, capturedEntry.gram, 0.0)
    }

    @Test
    fun `invoke preserves PerPiece entryType when reusing entry`() {
        // Arrange
        val originalEntry = SugarEntry(
            id = 2,
            currentTimestamp = 67890L,
            date = "2025-02-02",
            category = "Cookie",
            entryType = GramCountMode.PerPiece, // This triggers the 'else' branch
            gram = 15.0,
            quantity = 2.0,
            gramTotal = 30.0
        )

        // Act
        useCase.invoke(originalEntry)

        // Assert
        verify(exactly = 1) { mockDao.insertSugarEntry(any()) }

        // Assert that the entryType of the captured SugarEntry is correct
        val capturedEntry = sugarEntrySlot.captured
        assertEquals(GramCountMode.PerPiece, capturedEntry.entryType)

        // Assert that other important data was preserved
        assertEquals(originalEntry.category, capturedEntry.category)
        assertEquals(originalEntry.gram, capturedEntry.gram, 0.0)
    }
}

