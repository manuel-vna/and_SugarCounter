package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import android.content.SharedPreferences
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckThresholdResult
import com.jumparoundcreations.mva_sugarcounter.util.GeneralConstants
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class CheckDailyGramThresholdUseCaseTest : KoinTest {

    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockDao: DaoAppDatabase
    private lateinit var mockSharedPrefs: SharedPreferences
    private lateinit var checkDailyGramThresholdUseCase: CheckDailyGramThresholdUseCase

    @Before
    fun setUp() {
        // Stop any existing Koin instance to ensure a clean slate for each test
        stopKoin()

        // Create mock instances
        mockDatabase = mockk()
        mockDao = mockk()
        mockSharedPrefs = mockk()

        // Define mock behavior: when appDatabase.appDao() is called, return our mock DAO
        every { mockDatabase.appDao() } returns mockDao

        // Start a Koin instance specifically for this test, providing the mock SharedPreferences
        startKoin {
            modules(module {
                single { mockSharedPrefs }
            })
        }

        // Instantiate the class under test with its mocked dependency
        checkDailyGramThresholdUseCase = CheckDailyGramThresholdUseCase(mockDatabase)
    }

    @Test
    fun `invoke() returns WithinDailyThresholdBoundaries when database sum is below threshold`() {
        // --- Arrange ---
        val threshold = 50
        val databaseSum = 30

        // Mock the SharedPreferences to return our defined threshold
        every {
            mockSharedPrefs.getInt(
                GeneralConstants.KEY_GRAM_THRESHOLD,
                GeneralConstants.STANDARD_GRAM_THRESHOLD
            )
        } returns threshold

        // Mock the DAO to return a sum that is less than the threshold
        every { mockDao.getGramSumForSpecificDate(any()) } returns databaseSum

        // Create a dummy state object. The date is arbitrary but needed for the method call.
        val testState = EntrySavingStates(dateOfEntryEpochSec = 1733989200L) // Dec 12, 2024

        // --- Act ---
        val result = checkDailyGramThresholdUseCase.invoke(testState)

        // --- Assert ---
        assertEquals(CheckThresholdResult.WithinDailyThresholdBoundaries, result)
    }

    @Test
    fun `invoke() returns DailyThresholdBreached when database sum is above threshold`() {
        // --- Arrange ---
        val threshold = 50
        val databaseSum = 75 // This value is greater than the threshold

        // Mock the SharedPreferences to return our defined threshold
        every {
            mockSharedPrefs.getInt(
                GeneralConstants.KEY_GRAM_THRESHOLD,
                GeneralConstants.STANDARD_GRAM_THRESHOLD
            )
        } returns threshold

        // Mock the DAO to return a sum that is greater than the threshold
        every { mockDao.getGramSumForSpecificDate(any()) } returns databaseSum

        val testState = EntrySavingStates(dateOfEntryEpochSec = 1733989200L) // Dec 12, 2024

        // --- Act ---
        val result = checkDailyGramThresholdUseCase.invoke(testState)

        // --- Assert ---
        assertEquals(CheckThresholdResult.DailyThresholdBreached, result)
    }

    @Test
    fun `invoke() handles null from database as zero and stays within threshold`() {
        // --- Arrange ---
        val threshold = 50

        // Mock SharedPreferences
        every {
            mockSharedPrefs.getInt(
                GeneralConstants.KEY_GRAM_THRESHOLD,
                GeneralConstants.STANDARD_GRAM_THRESHOLD
            )
        } returns threshold

        // Mock the DAO to return null, simulating a day with no entries yet
        every { mockDao.getGramSumForSpecificDate(any()) } returns null

        val testState = EntrySavingStates(dateOfEntryEpochSec = 1733989200L)

        // --- Act ---
        val result = checkDailyGramThresholdUseCase.invoke(testState)

        // --- Assert ---
        // The use case should treat null as 0, which is less than the threshold
        assertEquals(CheckThresholdResult.WithinDailyThresholdBoundaries, result)
    }
}