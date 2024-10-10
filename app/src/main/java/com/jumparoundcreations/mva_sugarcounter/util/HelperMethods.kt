package com.jumparoundcreations.mva_sugarcounter.util


import android.content.Context
import android.text.format.DateUtils
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.data.ExportData.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class HelperMethods : KoinComponent {

    companion object {

        fun groupCounterItemsInGroupsByDay(savedEntries: List<Entry>): List<EntryGroup> {

            lateinit var todayOrYesterday: TodayOrYesterday
            val tempGroupedEntriesByDay =
                mutableMapOf<Pair<String, String>, MutableList<Entry>>()
            val groupedEntriesByDay = mutableListOf<EntryGroup>()

            // This intermediate step prepares the data for further processing
            // It creates group entries within a map, grouped by day.
            for (item in savedEntries) {
                todayOrYesterday = timestampIsTodayOrYesterday(item.currentTimestamp)
                val date = item.date
                val dayDisplayFormat =
                    if (todayOrYesterday == TodayOrYesterday.LATER) formatDateToString(
                        item.currentTimestamp,
                        "EEEE (dd.MM.)"
                    ) else todayOrYesterday.name


                tempGroupedEntriesByDay.computeIfAbsent(
                    Pair(
                        date,
                        dayDisplayFormat
                    )
                ) { mutableListOf() }.add(item)
            }

            // Move grouped data of map (data type: Map<Pair<String, String>, List<Entry>>) into a List<EntryGroup>
            // This data structure makes it easier to work with the data
            tempGroupedEntriesByDay.toList()
                .sortedByDescending { it.first.first }.forEach {
                    groupedEntriesByDay.add(
                        EntryGroup(
                            date = it.first.first,
                            dayDisplayFormat = it.first.second,
                            entryList = it.second
                        )
                    )
                }

            return groupedEntriesByDay
        }

        fun timestampIsTodayOrYesterday(currentTimestamp: Long): TodayOrYesterday {
            return if (DateUtils.isToday(currentTimestamp * 1000)) {
                TodayOrYesterday.TODAY
            } else if (DateUtils.isToday(currentTimestamp * 1000 + 86400000)) {
                TodayOrYesterday.YESTERDAY
            } else {
                TodayOrYesterday.LATER
            }
        }

        fun formatDateToString(currentTimestamp: Long, format: String): String {

            val simpleDateFormat2 = SimpleDateFormat(format)
            val localDate =
                Instant.ofEpochSecond(currentTimestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            val zoneId: ZoneId = ZoneId.systemDefault()
            val instant = localDate.atStartOfDay(zoneId).toInstant()

            return simpleDateFormat2.format(Date.from(instant))
        }

        fun calculateTotalGramPerDayBlock(valueList: List<Entry>): Int {
            if (valueList.isNotEmpty()) {
                return valueList.map { it.gramTotal }.reduce { sum, element -> sum + element }
            } else {
                return 0
            }
        }

        fun getSystemLanguage(): String {
            return Locale.getDefault().language
        }

        fun checkForUIMode(context: Context): Int {
            //darkMode == 33 and brightMode = 17
            return context.resources.configuration.uiMode
        }

        //Testing Purposes: START
        fun actionAddTestData() {
            GlobalScope.launch(Dispatchers.IO) {
                var timestamp = 1600617740.toLong()
                repeat((365 * 4)) {
                    timestamp += 86400

                    repeat((1..4).random()) {
                        var gramValue = Random.nextInt(from = 1, until = 20)

                        database.appDao().insertEntry(
                            Entry(
                                currentTimestamp = timestamp,
                                date = HelperMethods.formatDateToString(
                                    timestamp,
                                    "YYYY-MM-dd"
                                ),
                                category = "Test",
                                isPerHundred = true,
                                perPieceGram = gramValue,
                                perPieceAmount = 1,
                                perHundredGram = 0,
                                perHundredQuantity = 0,
                                gramTotal = gramValue
                            )
                        )
                    }
                }
            }
        }
        //Testing Purposes: END

    }

    enum class TodayOrYesterday {
        TODAY,
        YESTERDAY,
        LATER
    }

    enum class CountMode {
        SUGAR,
        CALORIES
    }

}