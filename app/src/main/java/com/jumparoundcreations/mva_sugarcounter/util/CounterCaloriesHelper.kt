package com.jumparoundcreations.mva_sugarcounter.util

import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.data.ExportData.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class CounterCaloriesHelper : KoinComponent {

    companion object {

        fun saveCaloriesEntryInDatabase(
            viewModelScope: CoroutineScope,
            category: String,
            dateOfEntryEpochSecValue: Long,
            caloriesInputValue: String
        ) {

            if (caloriesInputValue.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    database.appDao().insertEntryCalories(
                        EntryCalories(
                            currentTimestamp = dateOfEntryEpochSecValue,
                            date = HelperMethods.formatDateToString(
                                dateOfEntryEpochSecValue,
                                "YYYY-MM-dd"
                            ),
                            category = category,
                            caloriesTotal = caloriesInputValue.toInt()
                        )
                    )
                }
            }

        }

    }
}