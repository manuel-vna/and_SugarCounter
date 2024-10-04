package com.jumparoundcreations.mva_sugarcounter.util


import android.content.Context
import android.text.format.DateUtils
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class HelperMethodsTest {

    private lateinit var exampleListWithEntries: List<Entry>

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockContext: Context

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
        val entryGroup = HelperMethods.groupCounterItemsInGroupsByDay(exampleListWithEntries)
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
        val entryGroup = HelperMethods.groupCounterItemsInGroupsByDay(exampleListWithEntries)
        //Validate
        assertNotEquals(entryGroupControl, entryGroup)
    }

    @Test
    fun timestampIsTodayOrYesterday_checkToday() {

        mockkStatic(DateUtils::class)
        mockk<DateUtils>()
        every { DateUtils.isToday(any()) } returns true

        val timeString =
            HelperMethods.timestampIsTodayOrYesterday(1715801801) // 1715801801 = timestamp: Wednesday, May 15, 2024 21:36:41 GMT+02:00
        assertEquals(timeString, HelperMethods.TodayOrYesterday.TODAY)
    }

    @Test
    fun timestampIsTodayOrYesterday_checkLater() {


        mockkStatic(DateUtils::class)
        mockk<DateUtils>()
        every { DateUtils.isToday(any()) } returns false

        val timeString =
            HelperMethods.timestampIsTodayOrYesterday(1715801801) // 1715801801 = timestamp: Wednesday, May 15, 2024 21:36:41 GMT+02:00
        assertEquals(timeString, HelperMethods.TodayOrYesterday.LATER)
    }

    @Test
    fun formatDateToString() {
        val dateString = HelperMethods.formatDateToString(1716152163, "YYYY-MM-dd")
        assertEquals(dateString, "2024-05-19")
    }

    @Test
    fun calculateTotalGramPerDayBlock() {

        // gramTotal = 9 + 16 + 13 = 32
        val totalGramPerDayBlock =
            HelperMethods.calculateTotalGramPerDayBlock(exampleListWithEntries)
        assertEquals(totalGramPerDayBlock, 38)
    }

}