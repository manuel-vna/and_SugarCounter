package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.util.TimeConstants
import com.jumparoundcreations.mva_sugarcounter.util.roundToOneDecimal

class EditDatabaseEntryUseCase(
    val database: AppDatabase,
) {
    operator fun invoke(
        sugarEntryID: Int,
        sugarEntryType: GramCountMode,
        oldCategory: String,
        newCategory: String,
        newGram: Double,
        newQuantity: Double,
    ) {
        database.appDao().updateSugarEntry(
            id = sugarEntryID,
            gram = newGram,
            quantity = newQuantity,
            gramTotal =
                if (sugarEntryType == GramCountMode.PerHundred) {
                    ((newGram / 100) * newQuantity).roundToOneDecimal()
                } else {
                    (newGram * newQuantity).roundToOneDecimal()
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
