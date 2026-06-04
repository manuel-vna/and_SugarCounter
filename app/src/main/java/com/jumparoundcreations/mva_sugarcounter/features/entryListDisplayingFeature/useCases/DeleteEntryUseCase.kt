package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase

class DeleteEntryUseCase(
    private val database: AppDatabase,
) {
    operator fun invoke(entryId: Int) {
        database.appDao().deleteSpecificEntryRow(entryId)
    }
}
