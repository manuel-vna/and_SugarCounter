package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.categoryData.Category
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import kotlinx.coroutines.flow.Flow

class DisplayAllCategoriesUseCase(
    private val database: AppDatabase
) {

    operator fun invoke(): Flow<List<Category>> {
        return database.appDao().observeAllCategories()
    }

}
