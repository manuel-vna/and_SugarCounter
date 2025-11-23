package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GetEntryByCategoryResult

class GetEntryByCategoryUseCase(
    private val database: AppDatabase
) {

    suspend operator fun invoke(category: String): GetEntryByCategoryResult {
        val reply = database.appDao().checkIfGramValueExistsForCategory(category)

        return if (reply == null) {
            GetEntryByCategoryResult.NoEntryFound
        } else {
            GetEntryByCategoryResult.EntryFound(
                SugarEntry(
                    id = reply.id,
                    currentTimestamp = reply.currentTimestamp,
                    date = reply.date,
                    category = reply.category,
                    entryType = reply.entryType,
                    gram = reply.gram,
                    quantity = reply.quantity,
                    gramTotal = reply.gramTotal
                )
            )
        }
    }

}