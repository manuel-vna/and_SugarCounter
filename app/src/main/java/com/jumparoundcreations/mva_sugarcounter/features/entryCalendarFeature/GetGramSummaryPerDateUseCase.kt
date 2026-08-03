package com.jumparoundcreations.mva_sugarcounter.features.entryCalendarFeature

import com.jumparoundcreations.mva_sugarcounter.data.historyData.DateGramSummary
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import kotlinx.coroutines.flow.Flow


class GetGramSummaryPerDateUseCase(
    private val database: AppDatabase,
) {
    private val startOfToday = HelperMethods.getStartOfTodayAsLong()
    private val oneYearAgo = startOfToday-31557600L

    operator fun invoke(): Flow<List<DateGramSummary>> =
        database
            .appDao()
            .getGramSummariesByDate(sinceTimestamp = oneYearAgo)
}
