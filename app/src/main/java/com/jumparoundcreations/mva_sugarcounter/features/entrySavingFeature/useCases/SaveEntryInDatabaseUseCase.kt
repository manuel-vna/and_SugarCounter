package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.extensions.convertTimestampToDateString
import com.jumparoundcreations.mva_sugarcounter.util.extensions.roundToOneDecimal
import com.jumparoundcreations.mva_sugarcounter.util.extensions.toDoubleFormattedOrNull

class SaveEntryInDatabaseUseCase(
    private val database: AppDatabase,
) {
    suspend operator fun invoke(state: EntrySavingStates) {
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
                    state.entryFieldGramPerHundred.toDoubleFormattedOrNull(),
                gramPerPiece =
                    state.entryFieldGramPerPiece.toDoubleFormattedOrNull(),
                quantity =
                    state.entryFieldQuantity.toDoubleFormattedOrNull(),
                amount =
                    state.entryFieldAmount.toDoubleFormattedOrNull(),
                gramTotal = calculateGramTotal(state)
            ),
        )
    }

    private fun calculateGramTotal(state: EntrySavingStates): Double? {
        return when (state.gramCountMode) {
            GramCountMode.PerHundred -> {
                val gramPerHundred = state.entryFieldGramPerHundred.toDoubleFormattedOrNull() ?: return null
                val quantityPerPiece = state.entryFieldQuantity.toDoubleFormattedOrNull() ?: return null
                ((gramPerHundred / 100.0) * quantityPerPiece).roundToOneDecimal()
            }
            GramCountMode.PerPiece -> {
                val gramPerPiece = state.entryFieldGramPerPiece.toDoubleFormattedOrNull() ?: return null
                val amountPerPiece = state.entryFieldAmount.toDoubleFormattedOrNull() ?: return null
                (gramPerPiece * amountPerPiece).roundToOneDecimal()
            }
        }
    }
}
