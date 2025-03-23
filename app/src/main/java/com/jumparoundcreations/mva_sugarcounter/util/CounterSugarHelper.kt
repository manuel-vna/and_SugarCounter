package com.jumparoundcreations.mva_sugarcounter.util

import android.content.SharedPreferences
import android.util.Log
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.counterData.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.ExportData.database
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import kotlin.math.roundToInt

class CounterSugarHelper : KoinComponent {

    companion object {

        fun saveSugarEntryInDatabase(
            viewModelScope: CoroutineScope,
            sharedPrefsMain: SharedPreferences,
            counterVM: CounterVM,
            category: String,
            dateOfEntryEpochSecValue: Long,
            gramCountModeValue: GramCountMode,
            perHundredGramValue: String,
            perHundredQuantityValue: String,
            perPieceGramValue: String,
            perPieceAmountValue: String,
        ) {

            var perPieceGramInt = 0
            var perPieceAmountInt = 1
            var perHundredGramDouble = 0.0
            var perHundredQuantityDouble = 0.0

            if (gramCountModeValue == GramCountMode.PerHundred) {
                if (perHundredGramValue.isNotEmpty()) perHundredGramDouble =
                    perHundredGramValue.toDouble()
                if (perHundredQuantityValue.isNotEmpty()) perHundredQuantityDouble =
                    perHundredQuantityValue.toDouble()

                saveEntryInDatabase(
                    viewModelScope = viewModelScope,
                    sharedPrefsMain = sharedPrefsMain,
                    counterVM = counterVM,
                    category = category,
                    dateOfEntryEpochSecValue = dateOfEntryEpochSecValue,
                    isPerHundred = true,
                    perHundredGramInt = perHundredGramDouble.toInt(),
                    perHundredQuantityInt = perHundredQuantityDouble.toInt(),
                    perPieceGramInt = 0,
                    perPieceAmountInt = 0,
                    gramTotalInt = ((perHundredGramDouble / 100) * perHundredQuantityDouble).roundToInt()  // rule of three: Calculate sugar on basis of the quantity eaten
                )

            } else {
                if (perPieceGramValue.isNotEmpty()) perPieceGramInt = perPieceGramValue.toInt()
                if (perPieceAmountValue.isNotEmpty()) perPieceAmountInt =
                    perPieceAmountValue.toInt()

                saveEntryInDatabase(
                    viewModelScope = viewModelScope,
                    sharedPrefsMain = sharedPrefsMain,
                    counterVM = counterVM,
                    category = category,
                    dateOfEntryEpochSecValue = dateOfEntryEpochSecValue,
                    isPerHundred = false,
                    perHundredGramInt = 0,
                    perHundredQuantityInt = 0,
                    perPieceGramInt = perPieceGramInt,
                    perPieceAmountInt = perPieceAmountInt,
                    gramTotalInt = perPieceGramInt * perPieceAmountInt // multiplying gram per piece value with amount of itmes eaten
                )

            }
        }

        private fun saveEntryInDatabase(
            viewModelScope: CoroutineScope,
            sharedPrefsMain: SharedPreferences,
            counterVM: CounterVM,
            category: String,
            dateOfEntryEpochSecValue: Long,
            isPerHundred: Boolean,
            perHundredGramInt: Int,
            perHundredQuantityInt: Int,
            perPieceGramInt: Int,
            perPieceAmountInt: Int,
            gramTotalInt: Int
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                database.appDao().insertEntry(
                    Entry(
                        currentTimestamp = dateOfEntryEpochSecValue,
                        date = HelperMethods.convertTimestampToDateString(
                            dateOfEntryEpochSecValue,
                            "yyyy-MM-dd"
                        ),
                        category = category,
                        isPerHundred = isPerHundred,
                        perPieceGram = perPieceGramInt,
                        perPieceAmount = perPieceAmountInt,
                        perHundredGram = perHundredGramInt,
                        perHundredQuantity = perHundredQuantityInt,
                        gramTotal = gramTotalInt
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
                        counterVM.actionChangeAlertCaloriesThreshold(true)
                    }
                }
            }
        }

    }

}