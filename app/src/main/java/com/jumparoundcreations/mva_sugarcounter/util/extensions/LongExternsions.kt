package com.jumparoundcreations.mva_sugarcounter.util.extensions

import android.text.format.DateUtils
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods.TodayOrYesterday
import com.jumparoundcreations.mva_sugarcounter.util.TimeConstants
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.timestampIsTodayOrYesterday(): TodayOrYesterday =
    if (DateUtils.isToday(this * TimeConstants.MILLISECONDS_TO_SECONDS_DIVIDER)) {
        TodayOrYesterday.TODAY
    } else if (DateUtils.isToday(
            this *
                    TimeConstants.MILLISECONDS_TO_SECONDS_DIVIDER +
                    TimeConstants.DAY_ONE_IN_MILLISECONDS,
        )
    ) {
        TodayOrYesterday.YESTERDAY
    } else {
        TodayOrYesterday.LATER
    }


fun Long.convertTimestampToDateString(
    format: String,
): String {
    val formatter =
        DateTimeFormatter
            .ofPattern(format)
            .withZone(ZoneId.systemDefault()) // system's default timezone
    val instant = Instant.ofEpochSecond(this)

    return formatter.format(instant)
}

