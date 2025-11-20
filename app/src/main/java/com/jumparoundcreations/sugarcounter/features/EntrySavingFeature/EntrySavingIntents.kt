package com.jumparoundcreations.sugarcounter.features.EntrySavingFeature

sealed interface EntrySavingIntents {

    data object OpenAndCloseDatePicker : EntrySavingIntents
    data class ChangeSelectedDate(val epochTime: Long) : EntrySavingIntents

}