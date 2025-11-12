package com.jumparoundcreations.sugarcounter.util

import android.text.format.DateUtils
import com.jumparoundcreations.sugarcounter.data.EntryGroup
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

    private lateinit var exampleListWithEntries: List<Entry>

    @get:Rule
    val mockkRule = MockKRule(this)

    @Before
    fun setup() {
        exampleListWithEntries = listOf(
            Entry(
                id = 0,
                currentTimestamp = 1716233571,
                date = "2024-05-20",
                category = "Duplo",
                isPerHundred = false,
                perHundredGram = 0,
                perHundredQuantity = 0,
                perPieceGram = 9,
                perPieceAmount = 1,
                gramTotal = 9
            ),
            Entry(
                id = 0,
                currentTimestamp = 1716233783,
                date = "2024-05-20",
                category = "Ritter Sport",
                isPerHundred = true,
                perHundredGram = 48,
                perHundredQuantity = 33,
                perPieceGram = 0,
                perPieceAmount = 0,
                gramTotal = 16
            ),
            Entry(
                id = 0,
                currentTimestamp = 1716233801,
                date = "2024-05-20",
                category = "Kinder Maxi King",
                isPerHundred = false,
                perHundredGram = 0,
                perHundredQuantity = 0,
                perPieceGram = 13,
                perPieceAmount = 1,
                gramTotal = 13
            )
        )
    }

    @Test
    fun groupCounterItemsInGroupsByDayMatch() {
        //Prepare
        mockkStatic(DateUtils::class)
        mockk<DateUtils>()
        every { DateUtils.isToday(any()) } returns false

        val entryGroupControl = listOf(
            EntryGroup(
                date = "2024-05-20",
                dayDisplayFormat = "Monday (20.05.)",
                entryList = exampleListWithEntries
            )
        )
        //Action
        val entryGroup = HelperMethods.groupCounterItemsInGroupsByDay(
            exampleListWithEntries
        )
        //Validate
        assertEquals(entryGroupControl, entryGroup)
    }

    @Test
    fun groupCounterItemsInGroupsByDayNoMatch() {
        //Prepare
        mockkStatic(DateUtils::class)
        mockk<DateUtils>()
        every { DateUtils.isToday(any()) } returns false

        val entryGroupControl = listOf(
            EntryGroup(
                date = "2023-05-20", // date is different
                dayDisplayFormat = "Monday (20.05.)",
                entryList = exampleListWithEntries
            )
        )
        //Action
        val entryGroup = HelperMethods.groupCounterItemsInGroupsByDay(
            exampleListWithEntries
        )
        //Validate
        assertNotEquals(entryGroupControl, entryGroup)
    }

    @Test
    fun timestampIsTodayOrYesterday_checkToday() {

        mockkStatic(DateUtils::class)
        mockk<DateUtils>()
        every { DateUtils.isToday(any()) } returns true

        // 1715801801 = timestamp: Wednesday, May 15, 2024 21:36:41 GMT+02:00
        val timeString =
            HelperMethods.timestampIsTodayOrYesterday(1715801801)
        assertEquals(timeString, HelperMethods.TodayOrYesterday.TODAY)
    }

    @Test
    fun timestampIsTodayOrYesterday_checkLater() {


        mockkStatic(DateUtils::class)
        mockk<DateUtils>()
        every { DateUtils.isToday(any()) } returns false

        // 1715801801 = timestamp: Wednesday, May 15, 2024 21:36:41 GMT+02:00
        val timeString =
            HelperMethods.timestampIsTodayOrYesterday(1715801801)
        assertEquals(timeString, HelperMethods.TodayOrYesterday.LATER)
    }


    @Test
    @Ignore("Result of YYYY-MM-dd seems to have changed to 2024-12-29")
    fun formatDateToString() {
        val timestamp: Long = 1735495200 //= GMT: Sunday, December 29, 2024 18:00:00

        val dateString = HelperMethods.convertTimestampToDateString(
            timestamp,
            "yyyy-MM-dd"
        )
        assertEquals(dateString, "2024-12-29")
        // "yyyy" represents the calendar year.

        val dateString2 = HelperMethods.convertTimestampToDateString(
            timestamp,
            "YYYY-MM-dd"
        )
        // Not equal because it returns 2025-12-29 since "YYYY"
        // represents the year associated with the ISO week date system.
        assertNotEquals(dateString2, "2024-12-29")

    }

    @Test
    fun calculateTotalGramPerDayBlock() {

        // gramTotal = 9 + 16 + 13 = 32
        val totalGramPerDayBlock =
            HelperMethods.calculateTotalGramPerDayBlock(exampleListWithEntries)
        assertEquals(totalGramPerDayBlock, 38)
    }

}