package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import android.content.SharedPreferences
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckThresholdResult
import com.jumparoundcreations.mva_sugarcounter.util.GeneralConstants
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CheckDailyGramThresholdUseCase(
    private val database: AppDatabase,
) : KoinComponent {
    private val sharedPrefsMain by inject<SharedPreferences>()

    operator fun invoke(state: EntrySavingStates): CheckThresholdResult {
        val dateString =
            HelperMethods.convertTimestampToDateString(
                state.dateOfEntryEpochSec,
                "yyyy-MM-dd",
            )

        val databaseSum = database.appDao().getGramSumForSpecificDate(dateString) ?: 0

        if (databaseSum >
            sharedPrefsMain.getInt(
                GeneralConstants.KEY_GRAM_THRESHOLD,
                GeneralConstants.STANDARD_GRAM_THRESHOLD,
            )
        ) {
            return CheckThresholdResult.DailyThresholdBreached
        }
        return CheckThresholdResult.WithinDailyThresholdBoundaries
    }
}
