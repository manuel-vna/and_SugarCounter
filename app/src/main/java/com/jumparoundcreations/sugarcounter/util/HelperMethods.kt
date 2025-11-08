package com.jumparoundcreations.sugarcounter.util


import android.content.Context
import android.text.format.DateUtils
import com.jumparoundcreations.sugarcounter.data.Entry
import com.jumparoundcreations.sugarcounter.data.EntryCalories
import com.jumparoundcreations.sugarcounter.data.EntryGroup
import com.jumparoundcreations.sugarcounter.data.IEntry
import com.jumparoundcreations.sugarcounter.data.settingsData.ExportData.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random

class HelperMethods : KoinComponent {

    companion object {

        fun <T : IEntry> groupCounterItemsInGroupsByDay(savedEntries: List<T>): List<EntryGroup> {

            lateinit var todayOrYesterday: TodayOrYesterday
            val tempGroupedEntriesByDay =
                mutableMapOf<Pair<String, String>, MutableList<T>>()
            val groupedEntriesByDay = mutableListOf<EntryGroup>()

            // This intermediate step prepares the data for further processing
            // It creates group entries within a map, grouped by day.
            for (item in savedEntries) {
                todayOrYesterday = timestampIsTodayOrYesterday(item.currentTimestamp)
                val date = item.date
                val dayDisplayFormat =
                    if (todayOrYesterday == TodayOrYesterday.LATER) convertTimestampToDateString(
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
            return if (DateUtils.isToday(currentTimestamp * TimeConstants.MILLISECONDS_TO_SECONDS_DIVIDER)) {
                TodayOrYesterday.TODAY
            } else if (DateUtils.isToday(
                    currentTimestamp *
                            TimeConstants.MILLISECONDS_TO_SECONDS_DIVIDER +
                            TimeConstants.DAY_ONE_IN_MILLISECONDS
                )
            ) {
                TodayOrYesterday.YESTERDAY
            } else {
                TodayOrYesterday.LATER
            }
        }

        fun convertTimestampToDateString(timestamp: Long, format: String): String {

            val formatter = DateTimeFormatter
                .ofPattern(format)
                .withZone(ZoneId.systemDefault()) //system's default timezone
            val instant = Instant.ofEpochSecond(timestamp)

            return formatter.format(instant)
        }


        fun <T : IEntry> calculateTotalGramPerDayBlock(valueList: List<T>): Int {
            if (valueList.isNotEmpty()) {
                return valueList.map {
                    when (it) {
                        is Entry -> it.gramTotal
                        is EntryCalories -> it.caloriesTotal
                        else -> return 0
                    }
                }.reduce { sum, element -> sum + element }
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


        /**
         * This method creates test data and is only called for that purpose
         * The timestampInSeconds marks the start date and has to be in accordance to the chosen yearsTimespan
         * @param Takes timestampInSeconds that is used as the start date
         * @param Takes a number of years that define how many years are filled with data beginning at the start date
         * @return Unit
         */
        fun actionAddTestData(
            timestampInSeconds: Int,
            yearsTimespan: Int,
            sugarTestData: Boolean,
            caloriesTestData: Boolean
        ) {
            GlobalScope.launch(Dispatchers.IO) {
                var timestamp = timestampInSeconds.toLong() //1600617740.toLong()
                repeat((365 * yearsTimespan)) {
                    timestamp += 86400

                    repeat((1..4).random()) {
                        val gramValue = Random.nextInt(from = 1, until = 20)
                        val caloriesValue = Random.nextInt(from = 200, until = 1200)

                        if (sugarTestData) {
                            database.appDao().insertEntry(
                                Entry(
                                    currentTimestamp = timestamp,
                                    date = convertTimestampToDateString(
                                        timestamp,
                                        "yyyy-MM-dd"
                                    ),
                                    category = "TestSugar",
                                    isPerHundred = true,
                                    perPieceGram = gramValue,
                                    perPieceAmount = 1,
                                    perHundredGram = 0,
                                    perHundredQuantity = 0,
                                    gramTotal = gramValue
                                )
                            )
                        }

                        if (caloriesTestData) {
                            database.appDao().insertEntryCalories(
                                EntryCalories(
                                    currentTimestamp = timestamp,
                                    date = convertTimestampToDateString(
                                        timestamp,
                                        "yyyy-MM-dd"
                                    ),
                                    category = "TestCalories",
                                    caloriesPerPiece = 120,
                                    caloriesAmount = 2,
                                    caloriesTotal = caloriesValue
                                )
                            )
                        }
                    }

                }
            }
        }

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