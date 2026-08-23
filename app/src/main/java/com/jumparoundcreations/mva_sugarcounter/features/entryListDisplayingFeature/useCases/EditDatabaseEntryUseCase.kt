package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.util.TimeConstants
import com.jumparoundcreations.mva_sugarcounter.util.extensions.roundToOneDecimal

class EditDatabaseEntryUseCase(
    val database: AppDatabase,
) {
    suspend operator fun invoke(
        sugarEntryID: Int,
        sugarEntryType: GramCountMode,
        oldCategory: String,
        newCategory: String,
        newGramPerHundred: Double?,
        newGramPerPiece: Double?,
        newQuantity: Double?,
        newAmount: Double?,
    ) {
        val currentEntry = database.appDao().getSugarEntryById(sugarEntryID) ?: return

        val gramPerHundred = if ( newGramPerHundred != currentEntry.gramPerHundred) newGramPerHundred
        else { currentEntry.gramPerHundred  }

        val gramPerPiece = if ( newGramPerPiece != currentEntry.gramPerPiece) newGramPerPiece
        else { currentEntry.gramPerPiece }

        val quantity = if ( newQuantity != currentEntry.quantity) newQuantity
        else { currentEntry.quantity }

        val amount = if ( newAmount != currentEntry.amount) newAmount
        else { currentEntry.amount }

        val hasCategoryChange = newCategory != oldCategory

        val gramTotal = calculateGramTotal(
            sugarEntryType,
            gramPerHundred,
            gramPerPiece,
            quantity,
            amount
        )

        database.appDao().updateSugarEntry(
            id = sugarEntryID,
            gramPerHundred = gramPerHundred,
            gramPerPiece = gramPerPiece,
            quantity = quantity,
            amount = amount,
            gramTotal = gramTotal,
        )


        if (hasCategoryChange) {
            database.appDao().updateEntrySugarCategoryOfLastXDays(
                oldCategory = oldCategory,
                newCategory = newCategory,
                startPoint = HelperMethods.getStartOfTodayAsLong() - TimeConstants.NINETY_DAYS_IN_SECONDS,
                endPoint = HelperMethods.getEndOfTodayAsLong(),
            )

            database.appDao().updateCategoryOnEdit(
                oldCategory = oldCategory,
                newCategory = newCategory,
            )
        }
    }

    private fun calculateGramTotal(
        entryType: GramCountMode,
        gramPerHundred: Double?,
        gramPerPiece: Double?,
        quantity: Double?,
        amount: Double?,
    ): Double? {
        return when (entryType) {
            GramCountMode.PerHundred -> {
                val gram = gramPerHundred ?: return null
                val qty = quantity ?: return null
                ((gram / 100.0) * qty).roundToOneDecimal()
            }

            GramCountMode.PerPiece -> {
                val gram = gramPerPiece ?: return null
                val amt = amount ?: return null
                (gram * amt).roundToOneDecimal()
            }
        }
    }

}
