package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.sugarcounter.database.AppDatabase

class DeleteEntryUseCase(
    private val database: AppDatabase
) {
    operator fun invoke(entryId: Int) {
        database.appDao().deleteSpecificEntryRow(entryId)
    }
}