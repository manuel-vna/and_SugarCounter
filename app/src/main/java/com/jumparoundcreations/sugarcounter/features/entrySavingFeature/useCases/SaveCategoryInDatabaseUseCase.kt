package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingStates

class SaveCategoryInDatabaseUseCase(
    private val database: AppDatabase
) {

    suspend operator fun invoke(state: EntrySavingStates) {


    }
}
