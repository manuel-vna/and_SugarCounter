package com.jumparoundcreations.mva_sugarcounter.util

import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.ExportData.database
import com.jumparoundcreations.mva_sugarcounter.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToInt

class CounterSugarHelper : KoinComponent {

    private val database by inject<AppDatabase>()

    companion object {

        fun checkModeForSugarSaving(
            viewModelScope: CoroutineScope,
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

                if (perHundredGramValue.isEmpty()) {
                    //_alertDialog.value = true
                    counterVM.actionChangeAlertDialogValue(true)
                } else {
                    saveEntryInDatabase(
                        viewModelScope,
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
                }
            } else {
                if (perPieceGramValue.isNotEmpty()) perPieceGramInt = perPieceGramValue.toInt()
                if (perPieceAmountValue.isNotEmpty()) perPieceAmountInt =
                    perPieceAmountValue.toInt()

                if (perPieceGramValue.isEmpty()) {
                    //_alertDialogValue = true
                    counterVM.actionChangeAlertDialogValue(true)
                } else {
                    saveEntryInDatabase(
                        viewModelScope,
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
        }


        private fun saveEntryInDatabase(
            viewModelScope: CoroutineScope,
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
                        date = HelperMethods.formatDateToString(
                            dateOfEntryEpochSecValue,
                            "YYYY-MM-dd"
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

                counterVM.checkGramThreshold()

            }
        }

    }

}