package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.sugarcounter.util.HelperMethods

class ReuseEntryForTodayUseCase(
    val database: AppDatabase
) {
    operator fun invoke(entrySugar: SugarEntry) {

        val currentTimestamp = System.currentTimeMillis() / 1000

        if (entrySugar.entryType == GramCountMode.PerHundred) {
            database.appDao().insertSugarEntry(
                SugarEntry(
                    currentTimestamp = currentTimestamp,
                    date = HelperMethods.convertTimestampToDateString(
                        currentTimestamp,
                        "yyyy-MM-dd"
                    ),
                    category = entrySugar.category,
                    entryType = GramCountMode.PerHundred,
                    gram = entrySugar.gram,
                    quantity = entrySugar.quantity,
                    gramTotal = entrySugar.gramTotal
                )
            )
        } else {
            database.appDao().insertSugarEntry(
                SugarEntry(
                    currentTimestamp = currentTimestamp,
                    date = HelperMethods.convertTimestampToDateString(
                        currentTimestamp,
                        "yyyy-MM-dd"
                    ),
                    category = entrySugar.category,
                    entryType = GramCountMode.PerPiece,
                    gram = entrySugar.gram,
                    quantity = entrySugar.quantity,
                    gramTotal = entrySugar.gramTotal
                )
            )
        }
    }

}