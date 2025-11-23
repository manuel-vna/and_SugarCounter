package com.jumparoundcreations.sugarcounter.util

import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode

object DatabaseConstants {
    const val DEFAULT_DATABASE_INT = 0

    const val DEFAULT_DATABASE_DOUBLE = 0.0
    const val DEFAULT_DATABASE_INT_AMOUNT = 1
    const val DEFAULT_DATABASE_STRING = ""
    const val DEFAULT_DATABASE_TIMESTAMP = 1L
    const val DEFAULT_DATABASE_BOOLEAN = false

    val DEFAULT_GRAM_COUNT_MODE = GramCountMode.PerHundred
}

object GeneralConstants {
    const val COLOR_RGB_MULTIPLIER = 255
    const val NULL_AS_DOUBLE = 0.0

    const val ONE_AS_INT = 1
}

object TimeConstants {
    const val MILLISECONDS_TO_SECONDS_DIVIDER = 1000
    const val MONTH_ONE_IN_MILLISECONDS = 2629743000
    const val DAY_ONE_IN_MILLISECONDS = 86400000

    const val DATE_SHORT_DAY = " EE dd.MM."

}

object TestData {
    const val TEST_BARCODE_NUMBER = "123456789"
}