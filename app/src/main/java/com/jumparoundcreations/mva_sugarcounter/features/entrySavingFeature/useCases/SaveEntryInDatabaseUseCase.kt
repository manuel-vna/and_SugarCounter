package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.NumberConstants
import com.jumparoundcreations.mva_sugarcounter.util.extensions.convertTimestampToDateString
import com.jumparoundcreations.mva_sugarcounter.util.extensions.roundToOneDecimal
import com.jumparoundcreations.mva_sugarcounter.util.extensions.toDoubleFormatted

class SaveEntryInDatabaseUseCase(
    private val database: AppDatabase,
) {
    operator fun invoke(state: EntrySavingStates) {
        database.appDao().insertSugarEntry(
            SugarEntry(
                currentTimestamp = state.dateOfEntryEpochSec,
                date =
                    state.dateOfEntryEpochSec.convertTimestampToDateString(
                        "yyyy-MM-dd",
                    ),
                category = state.categoryInField.trim(),
                entryType = state.gramCountMode,
                gramPerHundred =
                    if (state.entryFieldGramPerHundred.isEmpty()) {
                        NumberConstants.ONE_AS_DOUBLE
                    } else {
                        state.entryFieldGramPerHundred.toDoubleFormatted()
                    },
                gramPerPiece =
                    if (state.entryFieldGramPerPiece.isEmpty()) {
                        NumberConstants.ONE_AS_DOUBLE
                    } else {
                        state.entryFieldGramPerPiece.toDoubleFormatted()
                    },
                quantity =
                    if (state.entryFieldQuantity.isEmpty()) {
                        NumberConstants.ONE_AS_DOUBLE
                    } else {
                        state.entryFieldQuantity.toDoubleFormatted()
                    },
                amount =
                    if (state.entryFieldAmount.isEmpty()) {
                        NumberConstants.ONE_AS_DOUBLE
                    } else {
                        state.entryFieldAmount.toDoubleFormatted()
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
