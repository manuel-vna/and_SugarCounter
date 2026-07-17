package com.jumparoundcreations.mva_sugarcounter.features.useCases

import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.util.extensions.formatDateForDisplay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetEntryGroupPerDayUseCase(
    private val database: AppDatabase,
) {
    private val startOfToday = HelperMethods.getStartOfTodayAsLong()
    private val endOfToday = HelperMethods.getEndOfTodayAsLong()

    operator fun invoke(timeFrameBeginning: Long): Flow<List<EntryGroup>> =
        database
            .appDao()
            .getEntriesInTimeframe((startOfToday - timeFrameBeginning), endOfToday)
            .map { entries ->
                entries
                    .groupBy { it.date }
                    .map { (date, items) ->
                        EntryGroup(
                            date = date,
                            dayDisplayFormat = date.formatDateForDisplay(),
                            entryList = items.sortedBy { it.currentTimestamp },
                        )
                    }.sortedByDescending { it.date }
            }
}
