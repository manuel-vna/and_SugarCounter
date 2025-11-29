package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.sugarcounter.data.categoryData.Category
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import kotlinx.coroutines.flow.Flow

class DisplayAllCategoriesUseCase(
    private val database: AppDatabase
) {

    operator fun invoke(): Flow<List<Category>> {
        return database.appDao().observeAllCategories()
    }

}
