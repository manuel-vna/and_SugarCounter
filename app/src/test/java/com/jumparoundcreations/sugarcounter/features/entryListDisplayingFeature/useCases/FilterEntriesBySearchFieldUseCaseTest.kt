package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.sugarcounter.data.EntryGroup
import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FilterEntriesBySearchFieldUseCaseTest {

    private lateinit var useCase: FilterEntriesBySearchFieldUseCase

    private lateinit var entryGroupList: List<EntryGroup>
    private val chocolateEntry = SugarEntry(
        id = 1,
        currentTimestamp = 0,
        date = "2025-12-20",
        category = "Chocolate Bar",
        entryType = GramCountMode.PerPiece,
        gram = 10.0,
        quantity = 1.0,
        gramTotal = 10.0
    )
    private val cookieEntry = SugarEntry(
        id = 2,
        currentTimestamp = 0,
        date = "2025-12-20", category = "Cookie",
        entryType = GramCountMode.PerPiece,
        gram = 15.0,
        quantity = 1.0,
        gramTotal = 15.0
    )
    private val yogurtEntry = SugarEntry(
        id = 3,
        currentTimestamp = 0,
        date = "2025-12-19",
        category = "Yogurt",
        entryType = GramCountMode.PerHundred,
        gram = 5.0,
        quantity = 150.0,
        gramTotal = 7.5
    )

    @Before
    fun setUp() {
        useCase = FilterEntriesBySearchFieldUseCase()

        entryGroupList = listOf(
            EntryGroup(
                date = "2025-12-20",
                dayDisplayFormat = "",
                entryList = listOf(chocolateEntry, cookieEntry)
            ),
            EntryGroup(
                date = "2025-12-19",
                dayDisplayFormat = "",
                entryList = listOf(yogurtEntry)
            )
        )
    }

    @Test
    fun `invoke returns only matching entry groups when search text is found`() {
        // --- Arrange ---
        val searchText = "Chocolate"

        // --- Act ---
        val filteredList = useCase(searchFieldText = searchText, entryList = entryGroupList)

        // --- Assert ---
        // We expect only one group to remain (the one from "2025-12-20")
        assertEquals(1, filteredList.size)
        // Check that the remaining group is the correct one
        assertEquals("2025-12-20", filteredList[0].date)
        // Check that the inner list of the remaining group still contains all its original entries
        assertEquals(2, filteredList[0].entryList.size)
    }

    @Test
    fun `invoke returns correctly filtered list with case-insensitive search`() {
        // --- Arrange ---
        val searchText = "chocolate" // Lowercase search text

        // --- Act ---
        val filteredList = useCase(searchText, entryGroupList)

        // --- Assert ---
        // It should still find "Chocolate Bar" and return its group
        assertEquals(1, filteredList.size)
        assertEquals("2025-12-20", filteredList[0].date)
    }


    @Test
    fun `invoke returns an empty list when no category matches the search text`() {
        // --- Arrange ---
        val searchText = "NonExistentCategory"

        // --- Act ---
        val filteredList = useCase(searchText, entryGroupList)

        // --- Assert ---
        // No group should match, so the resulting list should be empty
        assertTrue(filteredList.isEmpty())
    }


    @Test
    fun `invoke returns the original full list when the search text is blank`() {
        // --- Arrange ---
        val searchText = "" // Blank search text

        // --- Act ---
        val filteredList = useCase(searchText, entryGroupList)

        // --- Assert ---
        // With a blank search, the original list should be returned unfiltered
        assertEquals(2, filteredList.size)
        assertEquals(entryGroupList, filteredList)
    }

}
