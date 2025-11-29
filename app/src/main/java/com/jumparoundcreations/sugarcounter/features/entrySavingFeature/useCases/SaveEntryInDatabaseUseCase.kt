package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.sugarcounter.util.HelperMethods
import com.jumparoundcreations.sugarcounter.util.NumberConstants

class SaveEntryInDatabaseUseCase(
    private val database: AppDatabase
) {

    operator fun invoke(state: EntrySavingStates) {

        database.appDao().insertSugarEntry(
            SugarEntry(
                currentTimestamp = state.dateOfEntryEpochSec,
                date = HelperMethods.convertTimestampToDateString(
                    state.dateOfEntryEpochSec,
                    "yyyy-MM-dd"
                ),
                category = state.categoryInField,
                entryType = state.gramCountMode,
                gram = if (state.entryFieldGram.isEmpty()) NumberConstants.ONE_AS_DOUBLE
                else state.entryFieldGram.toDouble(),
                quantity = if (state.entryFieldQuantity.isEmpty()) NumberConstants.ONE_AS_DOUBLE
                else state.entryFieldQuantity.toDouble(),
                gramTotal = if (state.gramCountMode == GramCountMode.PerHundred) {
                    (state.entryFieldGram.toDouble() / NumberConstants.HUNDRED_AS_DOUBLE) *
                            state.entryFieldQuantity.toDouble()
                } else {
                    state.entryFieldGram.toDouble() * state.entryFieldQuantity.toDouble()

                },
            )
        )
    }
}