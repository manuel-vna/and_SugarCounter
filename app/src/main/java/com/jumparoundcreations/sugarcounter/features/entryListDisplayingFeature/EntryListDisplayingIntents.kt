package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature

import com.jumparoundcreations.sugarcounter.data.SugarEntry

sealed interface EntryListDisplayingIntents {
    data class OpenCardDetails(val sugarEntry: SugarEntry) : EntryListDisplayingIntents
    data object Test : EntryListDisplayingIntents

}