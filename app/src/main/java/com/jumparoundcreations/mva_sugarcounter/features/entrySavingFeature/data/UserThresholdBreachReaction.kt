package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data

sealed class UserThresholdBreachReaction {
    data object KeepLastEnteredEntry : UserThresholdBreachReaction()

    data object DeleteLastEnteredEntry : UserThresholdBreachReaction()
}
