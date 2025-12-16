package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases

import com.jumparoundcreations.sugarcounter.data.EntryGroup
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.util.HelperMethods
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

class GetEntryGroupPerDay(
    private val database: AppDatabase
) {

    private val today = LocalDate.now()
    private val startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()

    private val endOfToday =
        today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() - 1
    private val startOfYesterday = startOfToday - 86400

    operator fun invoke(): Flow<List<EntryGroup>> {
        return database.appDao().getEntriesInTimeframe(startOfYesterday, endOfToday)
            .map { entries ->
                entries
                    .groupBy { it.date }
                    .map { (date, items) ->
                        EntryGroup(
                            date = date,
                            dayDisplayFormat = HelperMethods.formatDateForDisplay(date),
                            entryList = items.sortedBy { it.currentTimestamp }
                        )
                    }
                    .sortedBy { it.date }
            }
    }

}

/*
val savedSugarCountGrouped: StateFlow<List<EntryGroup>> =
        database.appDao().getEntriesInTimeframe(startOfYesterday, endOfToday)
            .map { entries ->
                entries
                    .groupBy { it.date }
                    .map { (date, items) ->
                        EntryGroup(
                            date = date,
                            dayDisplayFormat = HelperMethods.formatDateForDisplay(date),
                            entryList = items.sortedBy { it.currentTimestamp }
                        )
                    }
                    .sortedBy { it.date }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
 */