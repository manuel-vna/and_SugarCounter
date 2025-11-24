package com.jumparoundcreations.sugarcounter.features.entrySavingFeature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GetEntryByCategoryResult
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.ScanResult
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.GetEntryByCategoryUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.ScanBarcodeUseCase
import com.jumparoundcreations.sugarcounter.ui.events.ScanUiEvents
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class EntrySavingViewModel(
    private val scanBarcodeUseCase: ScanBarcodeUseCase,
    private val getEntryByCategoryUseCase: GetEntryByCategoryUseCase
) : ViewModel(), KoinComponent {

    private val _scanUiEvents = MutableSharedFlow<ScanUiEvents>()
    val scanUiEvents = _scanUiEvents.asSharedFlow()

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
            when (val result = scanBarcodeUseCase()) {
                is ScanResult.FoundCategoryForBarcode -> {
                    actionSetBarcodeState(result.barcode)
                    actionSetCategoryForBarcode(result.category)
                    getEntryByCategory(result.category)
                }

                is ScanResult.NoCategoryForBarcode -> {
                    actionSetBarcodeState(result.barcode)
                    _scanUiEvents.emit(value = ScanUiEvents.ScanResultNoCategoryForBarcode)
                    //_noBarcodeYetInfoTitle.value = true
                }

                is ScanResult.Failed -> {
                    _scanUiEvents.emit(value = ScanUiEvents.ScanResultFailed)
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


    private fun getEntryByCategory(category: String) {
        viewModelScope.launch {
            when (val result = getEntryByCategoryUseCase(category)) {

                is GetEntryByCategoryResult.NoEntryFound -> {
                    //actionNoDataForChosenCategorySnackbarShownChange(true)
                    _entrySavingStates.update { current ->
                        current.copy(
                            entryFieldGram = "",
                            entryFieldQuantity = "",
                            gramCountModeTabIndex = 0
                        )
                    }
                }

                is GetEntryByCategoryResult.EntryFound -> {
                    val entry = result.entry
                    _entrySavingStates.update { current ->
                        current.copy(
                            entryFieldGram = entry.gram.toString(),
                            entryFieldQuantity = entry.quantity.toString(),
                            gramCountModeTabIndex =
                                if (entry.entryType == GramCountMode.PerHundred) 0 else 1
                        )
                    }
                }
            }
        }
    }


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