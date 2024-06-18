package com.example.mva_sugarcounter.util


import android.content.Context
import android.text.format.DateUtils
import com.example.mva_sugarcounter.data.Entry
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


class HelperMethodsTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockContext: Context


    @Test
    fun timestampIsTodayOrYesterday_checkToday(){
        val helperMethods = HelperMethods(mockContext)

        mockkStatic(DateUtils::class)
        mockk<DateUtils>()
        every { DateUtils.isToday(any())  } returns true

        val timeString =
            helperMethods.timestampIsTodayOrYesterday(1715801801) // 1715801801 = timestamp: Wednesday, May 15, 2024 21:36:41 GMT+02:00
        assertEquals(timeString, HelperMethods.TodayOrYesterday.TODAY)
    }

    @Test
    fun timestampIsTodayOrYesterday_checkLater(){
        val helperMethods = HelperMethods(mockContext)

        mockkStatic(DateUtils::class)
        mockk<DateUtils>()
        every { DateUtils.isToday(any())  } returns false

        val timeString =
            helperMethods.timestampIsTodayOrYesterday(1715801801) // 1715801801 = timestamp: Wednesday, May 15, 2024 21:36:41 GMT+02:00
        assertEquals(timeString, HelperMethods.TodayOrYesterday.LATER)
    }

    @Test
    fun formatDateToString() {
        val helperMethods = HelperMethods(mockContext)
        val dateString = helperMethods.formatDateToString(1716152163, "YYYY-MM-dd")
        assertEquals(dateString, "2024-05-19")
    }

    @Test
    fun calculateTotalGramPerDayBlock() {
        val helperMethods = HelperMethods(mockContext)

        val exampleListWithEntries = listOf(
            Entry(
                id = 0,
                currentTimestamp = 1716233571,
                date = "2024-05-20",
                gramItem = 10,
                amount = 1,
                category = "Test 1",
                gramTotal = 10
            ),
            Entry(
                id = 0,
                currentTimestamp = 1716233783,
                date = "2024-05-20",
                gramItem = 2,
                amount = 3,
                category = "Test 1",
                gramTotal = 6
            ),
            Entry(
                id = 0,
                currentTimestamp = 1716233801,
                date = "2024-05-20",
                gramItem = 8,
                amount = 2,
                category = "Test 1",
                gramTotal = 16
            )
        ) // gramTotal = 10 + 6 + 16 = 32

        val totalGramPerDayBlock = helperMethods.calculateTotalGramPerDayBlock(exampleListWithEntries)
        assertEquals(totalGramPerDayBlock, 32)
    }

}