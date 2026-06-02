package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.util.NumberConstants
import com.jumparoundcreations.mva_sugarcounter.util.roundToOneDecimal
import com.jumparoundcreations.mva_sugarcounter.util.toDoubleFormatted

class SaveEntryInDatabaseUseCase(
    private val database: AppDatabase,
) {
    operator fun invoke(state: EntrySavingStates) {
        database.appDao().insertSugarEntry(
            SugarEntry(
                currentTimestamp = state.dateOfEntryEpochSec,
                date =
                    HelperMethods.convertTimestampToDateString(
                        state.dateOfEntryEpochSec,
                        "yyyy-MM-dd",
                    ),
                category = state.categoryInField.trim(),
                entryType = state.gramCountMode,
                gram =
                    if (state.entryFieldGram.isEmpty()) {
                        NumberConstants.ONE_AS_DOUBLE
                    } else {
                        state.entryFieldGram.toDoubleFormatted()
                    },
                quantity =
                    if (state.entryFieldQuantity.isEmpty()) {
                        NumberConstants.ONE_AS_DOUBLE
                    } else {
                        state.entryFieldQuantity.toDoubleFormatted()
                    },
                gramTotal =
                    if (state.gramCountMode == GramCountMode.PerHundred) {
                        (
                            (state.entryFieldGram.toDoubleFormatted() / NumberConstants.HUNDRED_AS_DOUBLE) *
                                state.entryFieldQuantity.toDoubleFormatted()
                        ).roundToOneDecimal()
                    } else {
                        (
                            state.entryFieldGram.toDoubleFormatted() *
                                state.entryFieldQuantity.toDoubleFormatted()
                        ).roundToOneDecimal()
                    },
            ),
        )
    }
}
