package com.jumparoundcreations.sugarcounter.util

import android.content.SharedPreferences
import android.util.Log
import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.data.settingsData.ExportData.database
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.sugarcounter.viewModels.CounterVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class CounterSugarHelper : KoinComponent {

    companion object {

        fun saveEntryInDatabase(
            viewModelScope: CoroutineScope,
            sharedPrefsMain: SharedPreferences,
            counterVM: CounterVM,
            category: String,
            dateOfEntryEpochSecValue: Long,
            entryType: GramCountMode,
            perHundredGram: Double,
            perHundredQuantity: Double,
            perPieceGram: Double,
            perPieceAmount: Double
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                database.appDao().insertSugarEntry(
                    SugarEntry(
                        currentTimestamp = dateOfEntryEpochSecValue,
                        date = HelperMethods.convertTimestampToDateString(
                            dateOfEntryEpochSecValue,
                            "yyyy-MM-dd"
                        ),
                        category = category.trim(),
                        entryType = entryType,
                        gram = if (entryType == GramCountMode.PerHundred) {
                            perHundredGram
                        } else {
                            perPieceGram
                        },
                        quantity = if (entryType == GramCountMode.PerHundred) {
                            perHundredQuantity
                        } else {
                            perPieceAmount
                        },
                        gramTotal = if (entryType == GramCountMode.PerHundred) {
                            (perHundredGram / 100) * perHundredQuantity
                        } else {
                            perPieceGram * perPieceAmount
                        },
                    )
                )

                checkThresholdForSugarInput(
                    sharedPrefsMain = sharedPrefsMain,
                    counterVM = counterVM,
                    viewModelScope = viewModelScope,
                    dateOfEntryEpochSec = dateOfEntryEpochSecValue
                )

            }
        }

        /**
         * This check method needs to be called within the IO thread of saving an entry.
         * Because the last entered gram data first needs to be added to the database and only then the check can
         * be done
         * @return Unit
         */
        fun checkThresholdForSugarInput(
            sharedPrefsMain: SharedPreferences,
            counterVM: CounterVM,
            viewModelScope: CoroutineScope,
            dateOfEntryEpochSec: Long
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                val dateString = HelperMethods.convertTimestampToDateString(
                    dateOfEntryEpochSec,
                    "yyyy-MM-dd"
                )
                val databaseSum = database.appDao().checkIfGramThresholdIsBreached(dateString) ?: 0
                Log.d("checkThresholdForSugarInput", "DatabaseSumSugar: $databaseSum")

                withContext(Dispatchers.Main) {
                    if (databaseSum > sharedPrefsMain.getInt("gramThresholdValue", 50)) {
                        counterVM.actionChangeAlertSugarThreshold(true)
                    }
                }
            }
        }

    }

}