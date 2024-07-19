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

        val exampleListWithEntries = listOf(
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
        ) // gramTotal = 9 + 16 + 13 = 32

        val totalGramPerDayBlock =
            HelperMethods.calculateTotalGramPerDayBlock(exampleListWithEntries)
        assertEquals(totalGramPerDayBlock, 38)
    }

}