package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.sugarcounter.util.HelperMethods
import com.jumparoundcreations.sugarcounter.util.TimeConstants

class EditDatabaseEntryUseCase(
    val database: AppDatabase
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
            gramTotal = if (sugarEntryType == GramCountMode.PerHundred) {
                (newGram / 100) * newQuantity
            } else {
                newGram * newQuantity
            }
        )

        database.appDao().updateEntrySugarCategoryOfLastXDays(
            oldCategory = oldCategory,
            newCategory = newCategory,
            startPoint = HelperMethods.getStartOfTodayAsLong() - TimeConstants.NINETY_DAYS_IN_SECONDS,
            endPoint = HelperMethods.getEndOfTodayAsLong()
        )

        database.appDao().updateCategoryOnEdit(
            oldCategory = oldCategory,
            newCategory = newCategory
        )
    }
}