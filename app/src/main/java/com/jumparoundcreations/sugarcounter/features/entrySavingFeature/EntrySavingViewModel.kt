package com.jumparoundcreations.sugarcounter.features.entrySavingFeature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.CheckThresholdResult
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.CheckUserInputResult
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GetEntryByCategoryResult
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.ScanResult
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.UserThresholdBreachReaction
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.CheckDailyGramThresholdUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.CheckForDefaultSavingValuesUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.CheckUserInputUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.DisplayAllCategoriesUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.GetEntryByCategoryUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.SaveCategoryInDatabaseUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.SaveEntryInDatabaseUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.ScanBarcodeUseCase
import com.jumparoundcreations.sugarcounter.ui.events.ScanUiEvents
import com.jumparoundcreations.sugarcounter.util.EntrySavingConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EntrySavingViewModel(
    private val scanBarcodeUseCase: ScanBarcodeUseCase,
    private val getEntryByCategoryUseCase: GetEntryByCategoryUseCase,
    private val saveEntryInDatabaseUseCase: SaveEntryInDatabaseUseCase,
    private val saveCategoryInDatabaseUseCase: SaveCategoryInDatabaseUseCase,
    private val checkUserInputUseCase: CheckUserInputUseCase,
    private val checkForDefaultSavingValuesUseCase: CheckForDefaultSavingValuesUseCase,
    private val displayAllCategoriesUseCase: DisplayAllCategoriesUseCase,
    private val checkDailyGramThresholdUseCase: CheckDailyGramThresholdUseCase
) : ViewModel(), KoinComponent {

    val database by inject<AppDatabase>()

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

            is EntrySavingIntents.EditOfCategoryField ->
                actionEditOfCategoryField(action.categoryInField, action.categoryDropdownExpanded)

            is EntrySavingIntents.ExpandOrCollapseCategoryDropdown ->
                actionExpandOrCollapseCategoryDropdown(action.categoryDropdownExpanded)

            is EntrySavingIntents.EditOfCategoryWithinDropdown ->
                actionEditOfCategoryWithinDropdown(
                    action.categoryInDropdown,
                    action.categoryDropdownExpanded
                )

            is EntrySavingIntents.ChangeGramCountMode ->
                actionChangeGramCountMode(action.gramCountMode)

            is EntrySavingIntents.ChangeGramCountModeTabIndex ->
                actionChangeGramCountModeTabIndex(action.tabIndex)

            is EntrySavingIntents.ChangeEntryFieldGram ->
                actionChangeEntryFieldGram(action.entryFieldGram)

            is EntrySavingIntents.ChangeEntryFieldQuantity ->
                actionChangeEntryFieldQuantity(action.entryFieldQuantity)

            is EntrySavingIntents.SaveSugarEntry ->
                actionSaveEntry()

            is EntrySavingIntents.DismissNoCategoryDataEnteredAlert ->
                actionDismissNoCategoryDataEnteredAlert()

            is EntrySavingIntents.DismissNoSugarDataEnteredAlert ->
                actionDismissNoSugarDataEnteredAlert()

            is EntrySavingIntents.ClearInputFields ->
                actionClearInputFields()

            is EntrySavingIntents.UserThresholdReaction ->
                actionHandleUserThresholdBreachReaction(
                    action.userThresholdBreachReaction
                )
        }
    }

    init {
        observeAllCategories()
    }

    private fun observeAllCategories() {
        viewModelScope.launch {
            displayAllCategoriesUseCase().collect { list ->
                val stringList = list.map { it.category }
                _entrySavingStates.update { old ->
                    old.copy(categoryListInDropdown = stringList)
                }
            }
        }
    }

    private fun actionChangeDatePickerVisibility() {
        _entrySavingStates.update {
            it.copy(
                datePickerShown = it.datePickerShown.not()
            )
        }
    }

    private fun actionChangeSelectedDate(epochSec: Long) {
        _entrySavingStates.update { current ->
            current.copy(
                dateOfEntryEpochSec = epochSec
            )
        }
    }

    private fun actionScanBarcode() {
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
                    //actionBarcodeNotPresentInDb()
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
                categoryInField = category
            )
        }
    }


    private fun getEntryByCategory(category: String) {
        viewModelScope.launch {
            when (val result = getEntryByCategoryUseCase(category)) {

                is GetEntryByCategoryResult.NoEntryFound -> {
                    _scanUiEvents.emit(value = ScanUiEvents.CategoryEditNoDataForChosenCategory)
                    _entrySavingStates.update { current ->
                        current.copy(
                            entryFieldGram = "",
                            entryFieldQuantity = ""
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

    private fun actionBarcodeNotPresentInDb() {
        _entrySavingStates.update { current ->
            current.copy(
                barcodeNotPresentInDb = current.barcodeNotPresentInDb.not()
            )
        }
    }

    private fun actionEditOfCategoryField(
        categoryInField: String,
        categoryDropdownExpanded: Boolean
    ) {
        _entrySavingStates.update { current ->
            current.copy(
                categoryInField = categoryInField,
                categoryDropdownExpanded = categoryDropdownExpanded
            )
        }
    }

    private fun actionExpandOrCollapseCategoryDropdown(
        categoryDropdownExpanded: Boolean
    ) {
        _entrySavingStates.update { current ->
            current.copy(
                categoryDropdownExpanded = categoryDropdownExpanded
            )
        }
    }

    private fun actionEditOfCategoryWithinDropdown(
        categoryInDropdown: String,
        categoryDropdownExpanded: Boolean
    ) {
        _entrySavingStates.update { current ->
            current.copy(
                categoryInField = categoryInDropdown,
                categoryDropdownExpanded = categoryDropdownExpanded
            )
        }
        getEntryByCategory(categoryInDropdown)
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
    }

    private fun actionChangeEntryFieldQuantity(entryFieldQuantity: String) {
        _entrySavingStates.update { current ->
            current.copy(
                entryFieldQuantity = entryFieldQuantity
            )
        }
    }

    private fun actionSaveEntry() {

        if (checkForDefaultSavingValuesUseCase(entrySavingStates.value)) {
            _entrySavingStates.update { current ->
                current.copy(
                    entryFieldQuantity = EntrySavingConstants.DEFAULT_PER_PIECE_VALUE
                )
            }
        }

        val checkUserInputResult = checkUserInputUseCase(entrySavingStates.value)

        when (checkUserInputResult) {
            CheckUserInputResult.NoCategoryGiven -> {
                _entrySavingStates.update { current ->
                    current.copy(
                        savingProcessMissingCategoryData = true
                    )
                }
            }

            CheckUserInputResult.NoGramDataGivenButCategoryGiven -> {
                viewModelScope.launch {
                    val currentState = entrySavingStates.value
                    withContext(Dispatchers.IO) {
                        saveCategoryInDatabaseUseCase(state = currentState)
                    }
                }
                actionSetBarcodeState(barcodeNumber = "")
                _entrySavingStates.update { current ->
                    current.copy(
                        savingProcessMissingSugarData = true
                    )
                }
            }

            CheckUserInputResult.InputDataComplete -> {
                viewModelScope.launch {
                    // Reading the state on the main thread
                    val currentState = entrySavingStates.value
                    // Switching to IO AFTER capturing the correct state
                    // Otherwise timing issues concerning state updates might appear
                    withContext(Dispatchers.IO) {
                        saveEntryInDatabaseUseCase(currentState)
                        saveCategoryInDatabaseUseCase(currentState)
                        val result = checkDailyGramThresholdUseCase(currentState)
                        if (result is CheckThresholdResult.DailyThresholdBreached) {
                            _entrySavingStates.update { current ->
                                current.copy(
                                    savingProcessDailyGramThreshold =
                                        CheckThresholdResult.DailyThresholdBreached
                                )
                            }
                        }
                    }
                }
                actionSetBarcodeState(barcodeNumber = "")
            }
        }

    }

    private fun actionDismissNoCategoryDataEnteredAlert() {
        _entrySavingStates.update { current ->
            current.copy(
                savingProcessMissingCategoryData = false
            )
        }
    }

    private fun actionDismissNoSugarDataEnteredAlert() {
        _entrySavingStates.update { current ->
            current.copy(
                savingProcessMissingSugarData = false
            )
        }
    }

    private fun actionClearInputFields() {
        _entrySavingStates.update { current ->
            current.copy(
                categoryInField = "",
                entryFieldGram = "",
                entryFieldQuantity = "",
                gramCountModeTabIndex = 0
            )
        }
    }

    private fun actionHandleUserThresholdBreachReaction(
        userThresholdBreachReaction: UserThresholdBreachReaction
    ) {
        when (userThresholdBreachReaction) {
            is UserThresholdBreachReaction.DeleteLastEnteredEntry -> {
                _entrySavingStates.update { current ->
                    current.copy(
                        savingProcessDailyGramThreshold = CheckThresholdResult.WithinDailyThresholdBoundaries
                    )
                }
                viewModelScope.launch(Dispatchers.IO) {
                    database.appDao().deleteLastEntry()
                }
            }

            is UserThresholdBreachReaction.KeepLastEnteredEntry -> {
                _entrySavingStates.update { current ->
                    current.copy(
                        savingProcessDailyGramThreshold = CheckThresholdResult.WithinDailyThresholdBoundaries
                    )
                }
            }
        }
    }

}