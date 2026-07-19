package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.util.TimeConstants
import com.jumparoundcreations.mva_sugarcounter.util.extensions.roundToOneDecimal

class EditDatabaseEntryUseCase(
    val database: AppDatabase,
) {
    operator fun invoke(
        sugarEntryID: Int,
        sugarEntryType: GramCountMode,
        oldCategory: String,
        newCategory: String,
        newGramPerHundred: Double,
        newGramPerPiece:Double,
        newQuantity: Double,
        newAmount: Double
    ) {
        database.appDao().updateSugarEntry(
            id = sugarEntryID,
            gramPerHundred = newGramPerHundred,
            gramPerPiece = newGramPerPiece,
            quantity = newQuantity,
            amount = newAmount,
            gramTotal =
                if (sugarEntryType == GramCountMode.PerHundred) {
                    ((newGramPerHundred / 100) * newQuantity).roundToOneDecimal()
                } else {
                    (newGramPerPiece * newAmount).roundToOneDecimal()
                },
        )

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
