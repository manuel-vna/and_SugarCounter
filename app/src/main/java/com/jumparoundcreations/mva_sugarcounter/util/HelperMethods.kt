package com.jumparoundcreations.mva_sugarcounter.util


import android.content.Context
import android.text.format.DateUtils
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntryIntTemp
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.ExportData.database
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
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

        fun calculateTotalGramPerDayBlockTemp(valueList: List<SugarEntryIntTemp>): Int {
            return if (valueList.isNotEmpty()) {
                valueList.map {
                    it.gramTotal
                }.reduce { sum, element -> sum + element }
            } else {
                0

            }
        }

        fun getSystemLanguage(): String {
            return Locale.getDefault().language
        }

        fun checkForUIMode(context: Context): Int {
            //darkMode == 33 and brightMode = 17
            return context.resources.configuration.uiMode
        }

        fun getStartOfTodayAsLong(): Long {
            return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
        }

        fun getEndOfTodayAsLong(): Long {
            return LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond() - 1
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

}