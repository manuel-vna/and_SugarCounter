package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data

sealed class CheckThresholdResult {

    data object DailyThresholdBreached : CheckThresholdResult()

    data object WithinDailyThresholdBoundaries : CheckThresholdResult()

}
