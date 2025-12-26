package com.jumparoundcreations.mva_sugarcounter.util

import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode

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
    const val EMPTY_STRING = ""
    const val STANDARD_GRAM_THRESHOLD = 50
    const val KEY_GRAM_THRESHOLD = "gramThresholdValue"
}

object NumberConstants {
    const val COLOR_RGB_MULTIPLIER = 255
    const val NULL_AS_DOUBLE = 0.0
    const val ONE_AS_INT = 1

    const val ONE_AS_DOUBLE = 1.0

    const val HUNDRED_AS_DOUBLE = 100.0

    const val CATEGORY_MAX_INPUT_CHARACTERS = 40
}

object TimeConstants {
    const val MILLISECONDS_TO_SECONDS_DIVIDER = 1000
    const val MONTH_ONE_IN_MILLISECONDS = 2629743000
    const val DAY_ONE_IN_MILLISECONDS = 86400000
    const val ONE_DAY_IN_SECONDS = 86400L
    const val NINETY_DAYS_IN_SECONDS = 7776000L

    const val YEAR_ONE_IN_SECONDS = 31536000L


    const val DATE_SHORT_DAY = " EE dd.MM."

}

object TestData {
    const val TEST_BARCODE_NUMBER = "123456789"
}

object EntrySavingConstants {

    const val DEFAULT_PER_PIECE_VALUE = "1"
}