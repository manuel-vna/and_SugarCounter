package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GetEntryByCategoryResult

class GetEntryByCategoryUseCase(
    private val database: AppDatabase,
) {
    suspend operator fun invoke(category: String): GetEntryByCategoryResult {
        val reply = database.appDao().checkIfEntryExistsForCategory(category)

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
                    gramPerHundred = reply.gramPerHundred,
                    gramPerPiece = reply.gramPerPiece,
                    quantity = reply.quantity,
                    amount = reply.amount,
                    gramTotal = reply.gramTotal,
                ),
            )
        }
    }
}
