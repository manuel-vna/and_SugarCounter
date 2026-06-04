package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GetEntryByCategoryResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetEntryByCategoryUseCaseTest {
    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockDao: DaoAppDatabase
    private lateinit var useCase: GetEntryByCategoryUseCase

    @Before
    fun setUp() {
        mockDatabase = mockk()
        mockDao = mockk()
        coEvery { mockDatabase.appDao() } returns mockDao
        useCase = GetEntryByCategoryUseCase(mockDatabase)
    }

    @Test
    fun `invoke returns EntryFound when database returns a SugarEntry`() =
        runTest {
            // Arrange
            val categoryToSearch = "Chocolate"
            val expectedEntry =
                SugarEntry(
                    id = 1,
                    currentTimestamp = System.currentTimeMillis(),
                    date = "2025-12-15",
                    category = categoryToSearch,
                    entryType = GramCountMode.PerPiece,
                    gram = 10.0,
                    quantity = 2.0,
                    gramTotal = 20.0,
                )

            // Mock the DAO to return the expected entry when called with the correct category
            coEvery { mockDao.checkIfEntryExistsForCategory(categoryToSearch) } returns expectedEntry

            // Act
            val result = useCase(categoryToSearch)

            // Assert
            assertTrue(result is GetEntryByCategoryResult.EntryFound)
            // Cast the result and verify that the data inside is the same as what the DAO returned
            val entryFoundResult = result as GetEntryByCategoryResult.EntryFound
            assertEquals(expectedEntry, entryFoundResult.entry)
        }

    @Test
    fun `invoke returns NoEntryFound when database returns null`() =
        runTest {
            // Arrange
            val categoryToSearch = "NonExistentCategory"

            // Mock the DAO to return null, simulating that no entry was found for this category
            coEvery { mockDao.checkIfEntryExistsForCategory(categoryToSearch) } returns null

            // Act
            val result = useCase(categoryToSearch)

            // Assert
            // Verify that the result is exactly the NoEntryFound singleton object
            assertEquals(GetEntryByCategoryResult.NoEntryFound, result)
        }
}
