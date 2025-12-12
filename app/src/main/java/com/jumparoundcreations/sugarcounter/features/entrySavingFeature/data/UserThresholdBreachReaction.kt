package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data

sealed class UserThresholdBreachReaction {

    data object KeepLastEnteredEntry : UserThresholdBreachReaction()

    data object DeleteLastEnteredEntry : UserThresholdBreachReaction()

}
