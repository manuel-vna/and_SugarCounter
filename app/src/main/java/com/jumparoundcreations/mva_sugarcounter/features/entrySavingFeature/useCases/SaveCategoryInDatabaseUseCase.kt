package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import android.util.Log
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.Category
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates

class SaveCategoryInDatabaseUseCase(
    private val database: AppDatabase
) {

    operator fun invoke(state: EntrySavingStates) {

        if (
            state.categoryListInDropdown.contains(state.categoryInField) &&
            state.barcodeNumber.isNotEmpty()
        ) {
            val categoryRow = database.appDao().getCategoryByCategoryName(
                category = state.categoryInField.trim()
            )
            categoryRow.barcodeNumber = state.barcodeNumber.trim()
            database.appDao().updateCategory(categoryRow)

        } else if (
            state.categoryListInDropdown.contains(state.categoryInField) &&
            state.barcodeNumber.isEmpty()
        ) {
            Log.d(
                "Category",
                "Category '${state.categoryInField}' already exists"
            )
        } else {
            database.appDao().insertCategory(
                Category(
                    category = state.categoryInField.trim(),
                    barcodeNumber = state.barcodeNumber.trim()
                )
            )
        }

    }
}
