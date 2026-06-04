package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data

sealed class CheckThresholdResult {
    data object DailyThresholdBreached : CheckThresholdResult()

    data object WithinDailyThresholdBoundaries : CheckThresholdResult()
}
