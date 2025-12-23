package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.util.NumberConstants
import com.jumparoundcreations.mva_sugarcounter.util.roundToOneDecimal

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
                category = state.categoryInField.trim(),
                entryType = state.gramCountMode,
                gram = if (state.entryFieldGram.isEmpty()) NumberConstants.ONE_AS_DOUBLE
                else state.entryFieldGram.toDouble(),
                quantity = if (state.entryFieldQuantity.isEmpty()) NumberConstants.ONE_AS_DOUBLE
                else state.entryFieldQuantity.toDouble(),
                gramTotal = if (state.gramCountMode == GramCountMode.PerHundred) {
                    ((state.entryFieldGram.toDouble() / NumberConstants.HUNDRED_AS_DOUBLE) *
                            state.entryFieldQuantity.toDouble()).roundToOneDecimal()
                } else {
                    (state.entryFieldGram.toDouble() * state.entryFieldQuantity.toDouble()
                            ).roundToOneDecimal()

                },
            )
        )
    }
}