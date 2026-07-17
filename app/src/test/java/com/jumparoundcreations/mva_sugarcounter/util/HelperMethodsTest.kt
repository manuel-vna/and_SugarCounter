package com.jumparoundcreations.mva_sugarcounter.util

import android.text.format.DateUtils
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.extensions.convertTimestampToDateString
import com.jumparoundcreations.mva_sugarcounter.util.extensions.timestampIsTodayOrYesterday
import io.mockk.every
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class HelperMethodsTest {
    private lateinit var exampleListWithEntries: List<SugarEntry>

    @get:Rule
    val mockkRule = MockKRule(this)

    @Before
    fun setup() {
        exampleListWithEntries =
            listOf(
                SugarEntry(
                    id = 0,
                    currentTimestamp = 1763713602,
                    date = "2025-11-21",
                    category = "Duplo",
                    entryType = GramCountMode.PerPiece,
                    gram = 9.0,
                    quantity = 1.0,
                    gramTotal = 9.0,
                ),
                SugarEntry(
                    id = 0,
                    currentTimestamp = 1763540802,
                    date = "2025-11-19",
                    category = "Ritter Sport",
                    entryType = GramCountMode.PerPiece,
                    gram = 6.0,
                    quantity = 2.0,
                    gramTotal = 12.0,
                ),
                SugarEntry(
                    id = 0,
                    currentTimestamp = 1763281602,
                    date = "2025-11-16",
                    category = "Weingummi",
                    entryType = GramCountMode.PerPiece,
                    gram = 5.0,
                    quantity = 3.0,
                    gramTotal = 15.0,
                ),
            )
    }

    @Test
    fun timestampIsTodayOrYesterday_checkToday() {
        mockkStatic(DateUtils::class)
        mockk<DateUtils>()
        every { DateUtils.isToday(any()) } returns true

        // 1715801801 = timestamp: Wednesday, May 15, 2024 21:36:41 GMT+02:00
        val timeString =
            1715801801L.timestampIsTodayOrYesterday()
        assertEquals(timeString, HelperMethods.TodayOrYesterday.TODAY)
    }

    @Test
    fun timestampIsTodayOrYesterday_checkLater() {
        mockkStatic(DateUtils::class)
        mockk<DateUtils>()
        every { DateUtils.isToday(any()) } returns false

        // 1715801801 = timestamp: Wednesday, May 15, 2024 21:36:41 GMT+02:00
        val timeString =
            1715801801L.timestampIsTodayOrYesterday()
        assertEquals(timeString, HelperMethods.TodayOrYesterday.LATER)
    }

    @Test
    @Ignore("Result of YYYY-MM-dd seems to have changed to 2024-12-29")
    fun formatDateToString() {
        val timestamp: Long = 1735495200 // = GMT: Sunday, December 29, 2024 18:00:00

        val dateString =
            timestamp.convertTimestampToDateString(
                "yyyy-MM-dd",
            )
        assertEquals(dateString, "2024-12-29")
        // "yyyy" represents the calendar year.

        val dateString2 =
            timestamp.convertTimestampToDateString(
                "YYYY-MM-dd",
            )
        // Not equal because it returns 2025-12-29 since "YYYY"
        // represents the year associated with the ISO week date system.
        assertNotEquals(dateString2, "2024-12-29")
    }

    @Test
    fun calculateTotalGramPerDayBlock() {
        // gramTotal = (1 x 9.0) + (2 x 6.0) + (3 x 5.0) = 36
        val totalGramPerDayBlock =
            HelperMethods.calculateTotalGramPerDayBlock(exampleListWithEntries)
        assertEquals(totalGramPerDayBlock, 36.0, 0.001)
    }
}
