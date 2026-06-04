package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.features.useCases.GetEntryGroupPerDayUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GetEntryGroupPerDayUseCaseTest {
    lateinit var database: AppDatabase
    lateinit var getEntryGroupPerDayUseCase: GetEntryGroupPerDayUseCase

    // Day Structure: 2x 2025-12-15 + 2x 205-12-16 + 1 x 2025-12-17 + 1 x 2025-12-18: 4 day groups
    val entryListFromDB =
        listOf(
            SugarEntry(
                id = 1,
                currentTimestamp = 123456789,
                date = "2025-12-15",
                category = "Duplo",
                entryType = GramCountMode.PerPiece,
                gram = 9.0,
                quantity = 2.0,
                gramTotal = 18.0,
            ),
            SugarEntry(
                id = 2,
                currentTimestamp = 123456788,
                date = "2025-12-15",
                category = "Snickers",
                entryType = GramCountMode.PerPiece,
                gram = 21.0,
                quantity = 1.0,
                gramTotal = 21.0,
            ),
            SugarEntry(
                id = 3,
                currentTimestamp = 123456787,
                date = "2025-12-16",
                category = "Schokolade",
                entryType = GramCountMode.PerHundred,
                gram = 46.0,
                quantity = 30.0,
                gramTotal = 14.4,
            ),
            SugarEntry(
                id = 4,
                currentTimestamp = 123456786,
                date = "2025-12-16",
                category = "Weingummi",
                entryType = GramCountMode.PerPiece,
                gram = 2.0,
                quantity = 8.0,
                gramTotal = 16.0,
            ),
            SugarEntry(
                id = 5,
                currentTimestamp = 123456785,
                date = "2025-12-17",
                category = "Pudding",
                entryType = GramCountMode.PerHundred,
                gram = 9.0,
                quantity = 2.0,
                gramTotal = 18.0,
            ),
            SugarEntry(
                id = 6,
                currentTimestamp = 123456784,
                date = "2025-12-18",
                category = "Kinder Maxi King",
                entryType = GramCountMode.PerPiece,
                gram = 13.0,
                quantity = 2.0,
                gramTotal = 26.0,
            ),
        )

    val successEntryGroupList =
        listOf(
            EntryGroup(
                date = "2025-12-18",
                dayDisplayFormat = "Thursday (18.12.)",
                entryList =
                    listOf(
                        SugarEntry(
                            id = 6,
                            currentTimestamp = 123456784,
                            date = "2025-12-18",
                            category = "Kinder Maxi King",
                            entryType = GramCountMode.PerPiece,
                            gram = 13.0,
                            quantity = 2.0,
                            gramTotal = 26.0,
                        ),
                    ),
            ),
            EntryGroup(
                date = "2025-12-17",
                dayDisplayFormat = "Wednesday (17.12.)",
                entryList =
                    listOf(
                        SugarEntry(
                            id = 5,
                            currentTimestamp = 123456785,
                            date = "2025-12-17",
                            category = "Pudding",
                            entryType = GramCountMode.PerHundred,
                            gram = 9.0,
                            quantity = 2.0,
                            gramTotal = 18.0,
                        ),
                    ),
            ),
            EntryGroup(
                date = "2025-12-16",
                dayDisplayFormat = "Tuesday (16.12.)",
                entryList =
                    listOf(
                        SugarEntry(
                            id = 4,
                            currentTimestamp = 123456786,
                            date = "2025-12-16",
                            category = "Weingummi",
                            entryType = GramCountMode.PerPiece,
                            gram = 2.0,
                            quantity = 8.0,
                            gramTotal = 16.0,
                        ),
                        SugarEntry(
                            id = 3,
                            currentTimestamp = 123456787,
                            date = "2025-12-16",
                            category = "Schokolade",
                            entryType = GramCountMode.PerHundred,
                            gram = 46.0,
                            quantity = 30.0,
                            gramTotal = 14.4,
                        ),
                    ),
            ),
            EntryGroup(
                date = "2025-12-15",
                dayDisplayFormat = "Monday (15.12.)",
                entryList =
                    listOf(
                        SugarEntry(
                            id = 2,
                            currentTimestamp = 123456788,
                            date = "2025-12-15",
                            category = "Snickers",
                            entryType = GramCountMode.PerPiece,
                            gram = 21.0,
                            quantity = 1.0,
                            gramTotal = 21.0,
                        ),
                        SugarEntry(
                            id = 1,
                            currentTimestamp = 123456789,
                            date = "2025-12-15",
                            category = "Duplo",
                            entryType = GramCountMode.PerPiece,
                            gram = 9.0,
                            quantity = 2.0,
                            gramTotal = 18.0,
                        ),
                    ),
            ),
        )

    @Before
    fun setup() {
        database = mockk<AppDatabase>()
        getEntryGroupPerDayUseCase = GetEntryGroupPerDayUseCase(database)

        coEvery {
            database.appDao().getEntriesInTimeframe(
                any(),
                any(),
            )
        } returns flowOf(entryListFromDB)
    }

    @Test
    fun `EntryGroups are correctly sorted per day`() {
        runTest {
            // Act
            val result = getEntryGroupPerDayUseCase(timeFrameBeginning = 12345L)

            // Assert
            assertEquals(
                expected = successEntryGroupList,
                actual = result.first(),
            )
            verify(exactly = 1) {
                database.appDao().getEntriesInTimeframe(
                    any(),
                    any(),
                )
            }
        }
    }
}
