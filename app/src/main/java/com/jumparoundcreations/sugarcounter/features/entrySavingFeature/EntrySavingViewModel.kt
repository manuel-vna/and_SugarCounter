package com.jumparoundcreations.sugarcounter.features.entrySavingFeature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.ScanResult
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.GetEntryByCategoryUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.ScanBarcodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class EntrySavingViewModel(
    private val scanBarcodeUseCase: ScanBarcodeUseCase,
    private val getEntryByCategoryUseCase: GetEntryByCategoryUseCase
) : ViewModel(), KoinComponent {

    private val _entrySavingStates = MutableStateFlow(EntrySavingStates())
    val entrySavingStates = _entrySavingStates.asStateFlow()

    fun onAction(action: EntrySavingIntents) {
        when (action) {
            is EntrySavingIntents.OpenAndCloseDatePicker ->
                actionChangeDatePickerVisibility()

            is EntrySavingIntents.ChangeSelectedDate ->
                actionChangeSelectedDate(action.epochTime)

            is EntrySavingIntents.ScanBarcode ->
                actionScanBarcode()

            is EntrySavingIntents.ChangeGramCountMode ->
                actionChangeGramCountMode(action.gramCountMode)

            is EntrySavingIntents.ChangeGramCountModeTabIndex ->
                actionChangeGramCountModeTabIndex(action.tabIndex)

            is EntrySavingIntents.ChangeEntryFieldGram ->
                actionChangeEntryFieldGram(action.entryFieldGram)

            is EntrySavingIntents.ChangeEntryFieldQuantity ->
                actionChangeEntryFieldQuantity(action.entryFieldQuantity)
        }
    }

    fun actionChangeDatePickerVisibility() {
        _entrySavingStates.update {
            it.copy(
                datePickerShown = it.datePickerShown.not()
            )
        }
    }

    fun actionChangeSelectedDate(epochSec: Long) {
        _entrySavingStates.update { current ->
            current.copy(
                dateOfEntryEpochSec = epochSec
            )
        }
    }

    fun actionScanBarcode() {
        viewModelScope.launch {
            val result = scanBarcodeUseCase()

            when (result) {
                is ScanResult.FoundCategoryForBarcode -> {
                    actionSetBarcodeState(result.barcode)
                    actionSetCategoryForBarcode(result.category)
                    //getEntryByCategory(result.category)
                }

                is ScanResult.NoCategoryForBarcode -> {
                    actionSetBarcodeState(result.barcode)

                    //_noBarcodeYetInfoTitle.value = true
                }

                is ScanResult.Failed -> {

                    //_noBarcodeYetInfoTitle.value = true
                    // optionally log or handle
                }
            }
        }
    }

    private fun actionSetBarcodeState(barcodeNumber: String) {
        _entrySavingStates.update { current ->
            current.copy(
                barcodeNumber = barcodeNumber
            )
        }
    }

    private fun actionSetCategoryForBarcode(category: String) {
        _entrySavingStates.update { current ->
            current.copy(
                categorySelected = category
            )
        }
    }

    /*
    private fun getEntryByCategory(category: String) {
        viewModelScope.launch {
            when (val result = getEntryByCategoryUseCase(category)) {

                is GetEntryByCategoryResult.NoEntryFound -> {
                    actionNoDataForChosenCategorySnackbarShownChange(true)
                    _entrySavingStates.update { current ->
                        current.copy(
                            // ToDo
                        )
                    }

                    _perPieceGram.value = ""
                    _perHundredGram.value = ""
                    _isHundredTabIndex.value = 0
                }

                is GetEntryByCategoryResult.EntryFound -> {
                    val entry = result.entry

                    when (entry.type) {
                        GramCountMode.PerPiece -> {
                            _perPieceGram.value = entry.gram.toString()
                            _perHundredGram.value = ""
                            _isHundredTabIndex.value = 1
                        }

                        GramCountMode.PerHundred -> {
                            _perHundredGram.value = entry.gram.toString()
                            _perPieceGram.value = ""
                            _isHundredTabIndex.value = 0
                        }
                    }
                }
            }
        }
    }
     */

    private fun actionChangeGramCountMode(gramCountMode: GramCountMode) {
        _entrySavingStates.update { current ->
            current.copy(
                gramCountMode = gramCountMode
            )
        }
    }

    private fun actionChangeGramCountModeTabIndex(tabIndex: Int) {
        _entrySavingStates.update { current ->
            current.copy(
                gramCountModeTabIndex = tabIndex
            )
        }
    }

    private fun actionChangeEntryFieldGram(entryFieldGram: String) {
        _entrySavingStates.update { current ->
            current.copy(
                entryFieldGram = entryFieldGram
            )
        }
        // When saving: entryFieldGram.replace(',', '.').toDoubleOrNull()
    }

    private fun actionChangeEntryFieldQuantity(entryFieldQuantity: String) {
        _entrySavingStates.update { current ->
            current.copy(
                entryFieldQuantity = entryFieldQuantity
            )
        }
    }

}