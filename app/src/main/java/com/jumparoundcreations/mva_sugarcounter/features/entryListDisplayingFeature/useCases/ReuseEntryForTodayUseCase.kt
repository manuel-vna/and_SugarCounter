package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.extensions.convertTimestampToDateString

class ReuseEntryForTodayUseCase(
    val database: AppDatabase,
) {
    operator fun invoke(entrySugar: SugarEntry) {
        val currentTimestamp = System.currentTimeMillis() / 1000

        if (entrySugar.entryType == GramCountMode.PerHundred) {
            database.appDao().insertSugarEntry(
                SugarEntry(
                    currentTimestamp = currentTimestamp,
                    date =
                        currentTimestamp.convertTimestampToDateString(
                            "yyyy-MM-dd",
                        ),
                    category = entrySugar.category,
                    entryType = GramCountMode.PerHundred,
                    gramPerHundred = entrySugar.gramPerHundred,
                    gramPerPiece = entrySugar.gramPerPiece,
                    quantity = entrySugar.quantity,
                    amount = entrySugar.amount,
                    gramTotal = entrySugar.gramTotal,
                ),
            )
        } else {
            database.appDao().insertSugarEntry(
                SugarEntry(
                    currentTimestamp = currentTimestamp,
                    date =
                        currentTimestamp.convertTimestampToDateString(
                            "yyyy-MM-dd",
                        ),
                    category = entrySugar.category,
                    entryType = GramCountMode.PerPiece,
                    gramPerHundred = entrySugar.gramPerHundred,
                    gramPerPiece = entrySugar.gramPerPiece,
                    quantity = entrySugar.quantity,
                    amount = entrySugar.amount,
                    gramTotal = entrySugar.gramTotal,
                ),
            )
        }
    }
}
