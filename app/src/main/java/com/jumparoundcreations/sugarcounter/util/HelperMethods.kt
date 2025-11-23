package com.jumparoundcreations.sugarcounter.util


import android.content.Context
import android.text.format.DateUtils
import com.jumparoundcreations.sugarcounter.data.EntryGroup
import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.data.settingsData.ExportData.database
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random

class HelperMethods : KoinComponent {

    companion object {

        fun formatDateForDisplay(date: String): String {
            return try {
                val input = LocalDate.parse(date)
                val formatter = DateTimeFormatter.ofPattern("EEEE (dd.MM.)")
                input.format(formatter)
            } catch (e: Exception) {
                date // fallback
            }
        }

        fun groupCounterItemsInGroupsByDay(savedEntries: List<SugarEntry>): List<EntryGroup> {

            lateinit var todayOrYesterday: TodayOrYesterday
            val tempGroupedEntriesByDay =
                mutableMapOf<Pair<String, String>, MutableList<SugarEntry>>()
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


        fun calculateTotalGramPerDayBlock(valueList: List<SugarEntry>): Double {
            return if (valueList.isNotEmpty()) {
                valueList.map {
                    it.gramTotal
                }.reduce { sum, element -> sum + element }
            } else {
                NumberConstants.NULL_AS_DOUBLE
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
        ) {
            GlobalScope.launch(Dispatchers.IO) {
                var timestamp = timestampInSeconds.toLong() //1600617740.toLong()
                repeat((365 * yearsTimespan)) {
                    timestamp += 86400

                    repeat((1..4).random()) {
                        val gramValue = Random.nextDouble(from = 1.0, until = 10.0)
                        val quantityValue = Random.nextDouble(from = 1.0, until = 8.0)

                        database.appDao().insertSugarEntry(
                            SugarEntry(
                                date = convertTimestampToDateString(
                                    timestamp,
                                    "yyyy-MM-dd"
                                ),
                                currentTimestamp = timestamp,
                                category = "TestSugar",
                                entryType = GramCountMode.PerHundred,
                                gram = gramValue,
                                quantity = quantityValue,
                                gramTotal = gramValue
                            )
                        )

                        database.appDao().insertSugarEntry(
                            SugarEntry(
                                date = convertTimestampToDateString(
                                    timestamp,
                                    "yyyy-MM-dd"
                                ),
                                currentTimestamp = timestamp,
                                category = "TestSugar",
                                entryType = GramCountMode.PerHundred,
                                gram = gramValue,
                                quantity = quantityValue,
                                gramTotal = gramValue
                            )
                        )
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