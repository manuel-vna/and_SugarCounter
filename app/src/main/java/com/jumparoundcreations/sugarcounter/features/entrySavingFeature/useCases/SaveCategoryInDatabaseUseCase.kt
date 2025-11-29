package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.sugarcounter.data.categoryData.Category
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingStates

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
