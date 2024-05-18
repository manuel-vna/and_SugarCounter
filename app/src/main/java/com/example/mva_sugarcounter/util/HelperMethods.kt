package com.example.mva_sugarcounter.util


import android.content.Context
import android.text.format.DateUtils
import com.example.mva_sugarcounter.data.Entry
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class HelperMethods(private val context: Context) {

    fun groupCounterItemsInGroupsByDay(savedEntry: List<Entry>): Map<Pair<String, String>, List<Entry>> {

        val groupedCounterItemsByDay =
            mutableMapOf<Pair<String, String>, MutableList<Entry>>()

        lateinit var todayOrYesterday: TodayOrYesterday

        for (item in savedEntry) {

            todayOrYesterday = timestampIsTodayOrYesterday(item.currentTimestamp)
            val date = item.date
            val dayDisplayFormat =
                if (todayOrYesterday == TodayOrYesterday.LATER) formatDateToString(
                    item.currentTimestamp,
                    "EEEE (dd.MM.)"
                ) else todayOrYesterday.name

            groupedCounterItemsByDay.computeIfAbsent(
                Pair(
                    date,
                    dayDisplayFormat
                )
            ) { mutableListOf() }.add(item)
        }
        return groupedCounterItemsByDay
    }

    private fun timestampIsTodayOrYesterday(currentTimestamp: Long): TodayOrYesterday {
        return if (DateUtils.isToday(currentTimestamp)) {
            TodayOrYesterday.TODAY
        } else if (DateUtils.isToday(currentTimestamp + 86400000)) {
            TodayOrYesterday.YESTERDAY
        } else {
            TodayOrYesterday.LATER
        }
    }

    enum class TodayOrYesterday {
        TODAY,
        YESTERDAY,
        LATER
    }

    fun formatDateToString(currentTimestamp: Long, format: String): String {

        val simpleDateFormat2 = SimpleDateFormat(format)
        val localDate =
            Instant.ofEpochMilli(currentTimestamp).atZone(ZoneId.systemDefault())
                .toLocalDate()
        val zoneId: ZoneId = ZoneId.systemDefault()
        val instant = localDate.atStartOfDay(zoneId).toInstant()

        return simpleDateFormat2.format(Date.from(instant))
    }

    fun calculateTotalGramPerDayBlock(valueList: List<Entry>): Int {
        return valueList.map { it.gramTotal }.reduce { sum, element -> sum + element }

    }

    fun getSystemLanguage(): String {
        return Locale.getDefault().language
    }


}