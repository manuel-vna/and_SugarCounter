package com.jumparoundcreations.mva_sugarcounter.util

import android.content.SharedPreferences
import android.util.Log
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.data.ExportData.database
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class CounterCaloriesHelper : KoinComponent {

    companion object {

        fun saveCaloriesEntryInDatabase(
            sharedPreferences: SharedPreferences,
            viewModelScope: CoroutineScope,
            counterVM: CounterVM,
            category: String,
            dateOfEntryEpochSecValue: Long,
            caloriesInputValue: String,
            caloriesAmountValue: String
        ) {

            var caloriesAmountInt = 1
            if (caloriesAmountValue.isNotEmpty()) caloriesAmountInt =
                caloriesAmountValue.toInt()

            if (caloriesInputValue.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    database.appDao().insertEntryCalories(
                        EntryCalories(
                            currentTimestamp = dateOfEntryEpochSecValue,
                            date = HelperMethods.convertTimestampToDateString(
                                dateOfEntryEpochSecValue,
                                "yyyy-MM-dd"
                            ),
                            category = category,
                            caloriesTotal = caloriesInputValue.toInt() * caloriesAmountInt
                        )
                    )

                    checkThresholdForCaloriesInput(
                        viewModelScope = viewModelScope,
                        counterVM = counterVM,
                        dateOfEntryEpochSec = dateOfEntryEpochSecValue,
                        sharedPrefsMain = sharedPreferences
                    )

                }
            }

        }

        /**
         * This check method needs to be called within the IO thread of saving an entry.
         * Because the last entered gram data first needs to be added to the database and only then the check can
         * be done
         * @return Unit
         */
        private fun checkThresholdForCaloriesInput(
            viewModelScope: CoroutineScope,
            counterVM: CounterVM,
            dateOfEntryEpochSec: Long,
            sharedPrefsMain: SharedPreferences
        ) {
            viewModelScope.launch(Dispatchers.IO) {

                val dateString = HelperMethods.convertTimestampToDateString(
                    dateOfEntryEpochSec,
                    "yyyy-MM-dd"
                )
                val databaseSumCalories =
                    database.appDao().checkIfCaloriesThresholdIsBreached(dateString) ?: 0
                Log.d(
                    "checkThresholdForCaloriesInput",
                    "DatabaseSumCalories: $databaseSumCalories"
                )

                withContext(Dispatchers.Main) {
                    if (databaseSumCalories > sharedPrefsMain.getInt(
                            "caloriesThresholdValue",
                            2250
                        )
                    ) {
                        counterVM._alertCaloriesThreshold.value = true
                    }
                }

            }
        }

    }
}