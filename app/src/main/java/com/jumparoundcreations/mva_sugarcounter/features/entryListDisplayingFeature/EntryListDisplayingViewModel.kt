package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases.DeleteEntryUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases.EditDatabaseEntryUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases.FilterEntriesBySearchFieldUseCase
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.useCases.ReuseEntryForTodayUseCase
import com.jumparoundcreations.mva_sugarcounter.features.useCases.GetEntryGroupPerDayUseCase
import com.jumparoundcreations.mva_sugarcounter.util.TimeConstants
import com.jumparoundcreations.mva_sugarcounter.util.extensions.toDoubleFormattedOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class EntryListDisplayingViewModel(
    val getEntryGroupPerDayUseCase: GetEntryGroupPerDayUseCase,
    val deleteEntryUseCase: DeleteEntryUseCase,
    val editDatabaseEntryUseCase: EditDatabaseEntryUseCase,
    val reuseEntryForTodayUseCase: ReuseEntryForTodayUseCase,
    val filterEntriesBySearchFieldUseCase: FilterEntriesBySearchFieldUseCase,
) : ViewModel(), KoinComponent {
    private val _entryListDisplayingStates = MutableStateFlow<EntryListDisplayingStates>(
        value = EntryListDisplayingStates.Loading,
    )
    val entryListDisplayingStates = _entryListDisplayingStates.asStateFlow()

    fun onAction(action: EntryListDisplayingIntents) {
        when (action) {
            is EntryListDisplayingIntents.OpenCardDetails -> {
                actionShowCardDetails(action.sugarEntry)
            }

            is EntryListDisplayingIntents.LoadEntryDataIntoCardDetails -> {
                actionLoadEntryDataIntoCardDetails(action.sugarEntry)
            }

            is EntryListDisplayingIntents.EditCategory -> {
                actionEditCategory(action.newCategory)
            }

            is EntryListDisplayingIntents.EditGram -> {
                actionEditGram(action.newGramPerHundred, action.newGramPerPiece)
            }

            is EntryListDisplayingIntents.EditQuantity -> {
                actionEditQuantity(action.newQuantity, action.newAmount)
            }

            is EntryListDisplayingIntents.DismissCardDetails -> {
                actionDismissCardDetails()
            }

            is EntryListDisplayingIntents.ShowDeleteEntryConfirmation -> {
                actionShowDeleteEntryConfirmation(action.isShown)
            }

            is EntryListDisplayingIntents.DeleteEntry -> {
                actionDeleteEntry(action.entryId)
            }

            is EntryListDisplayingIntents.EditEntryInDB -> {
                actionEditEntryInDB()
            }

            is EntryListDisplayingIntents.ReuseEntryForToday -> {
                actionReuseEntryForToday()
            }

            is EntryListDisplayingIntents.ChangeSearchFieldShown -> {
                actionChangeSearchFieldShown()
            }

            is EntryListDisplayingIntents.ChangeSearchTextFieldText -> {
                actionChangeSearchTextFieldText(action.newText)
            }

            is EntryListDisplayingIntents.FilterEntryListInHistory -> {
                actionFilterEntryListInHistory()
            }

            is EntryListDisplayingIntents.CloseSearchFieldAndClearText -> {
                actionCloseSearchFieldAndClearText()
            }
        }
    }

    init {
        groupEntriesPerDayCounter()
        groupEntriesPerDayHistory()
    }

    fun groupEntriesPerDayCounter() {
        viewModelScope.launch {
            getEntryGroupPerDayUseCase(
                timeFrameBeginning = TimeConstants.ONE_DAY_IN_SECONDS,
            ).catch { throwable ->
                _entryListDisplayingStates.update {
                    EntryListDisplayingStates.Error(
                        message = throwable.localizedMessage ?: "Unknown error",
                    )
                }
            }.collect { entryGroupListCounter ->
                _entryListDisplayingStates.update { current ->
                    if (current is EntryListDisplayingStates.Success) {
                        current.copy(
                            data = current.data.copy(
                                entriesGroupedPerDayCounter = entryGroupListCounter,
                            ),
                        )
                    } else {
                        EntryListDisplayingStates.Success(
                            data = SuccessData(
                                entriesGroupedPerDayCounter = entryGroupListCounter,
                            ),
                        )
                    }
                }
            }
        }
    }

    fun groupEntriesPerDayHistory() {
        _entryListDisplayingStates.update {
            EntryListDisplayingStates.Loading
        }

        viewModelScope.launch(Dispatchers.IO) {
            getEntryGroupPerDayUseCase(
                timeFrameBeginning = TimeConstants.TIMEFRAME_OF_SHOWN_ENTRY_CARDS,
            ).catch { throwable ->
                _entryListDisplayingStates.update {
                    EntryListDisplayingStates.Error(
                        message = throwable.localizedMessage ?: "Unknown error",
                    )
                }
            }.collectLatest { entryGroupListHistory ->
                _entryListDisplayingStates.update { current ->
                    if (current is EntryListDisplayingStates.Success) {
                        current.copy(
                            data = current.data.copy(
                                entriesGroupedPerDayHistory = entryGroupListHistory,
                                entriesGroupedPerDayUnfilteredHistory = entryGroupListHistory,
                            ),
                        )
                    } else {
                        EntryListDisplayingStates.Success(
                            data = SuccessData(
                                entriesGroupedPerDayHistory = entryGroupListHistory,
                                entriesGroupedPerDayUnfilteredHistory = entryGroupListHistory,
                            ),
                        )
                    }
                }
            }
        }
    }

    fun actionShowCardDetails(sugarEntry: SugarEntry) {
        _entryListDisplayingStates.update { current ->
            if (current is EntryListDisplayingStates.Success) {
                current.copy(
                    data = current.data.copy(
                        showCardItemBottomSheet = true,
                        entryInCardItem = sugarEntry,
                    ),
                )
            } else {
                current
            }
        }
    }

    fun actionLoadEntryDataIntoCardDetails(sugarEntry: SugarEntry) {
        _entryListDisplayingStates.update { current ->
            if (current is EntryListDisplayingStates.Success) {
                current.copy(
                    data = current.data.copy(
                        valueCategory = sugarEntry.category,
                        valueGramPerHundred = sugarEntry.gramPerHundred?.toString() ?: "",
                        valueGramPerPiece = sugarEntry.gramPerPiece?.toString() ?: "",
                        valueQuantity = sugarEntry.quantity?.toString() ?: "",
                        valueAmount = sugarEntry.amount?.toString() ?: "",
                    ),
                )
            } else {
                current
            }
        }
    }

    fun actionEditCategory(newCategory: String) {
        _entryListDisplayingStates.update { current ->
            if (current is EntryListDisplayingStates.Success) {
                current.copy(
                    data = current.data.copy(
                        valueCategory = newCategory,
                    ),
                )
            } else {
                current
            }
        }
    }

    fun actionEditGram(newGramPerHundred: String, newGramPerPiece: String) {
        _entryListDisplayingStates.update { current ->
            if (current is EntryListDisplayingStates.Success) {
                current.copy(
                    data = current.data.copy(
                        valueGramPerHundred = newGramPerHundred,
                        valueGramPerPiece = newGramPerPiece,
                    ),
                )
            } else {
                current
            }
        }
    }

    fun actionEditQuantity(newQuantity: String, newAmount: String) {
        _entryListDisplayingStates.update { current ->
            if (current is EntryListDisplayingStates.Success) {
                current.copy(
                    data = current.data.copy(
                        valueQuantity = newQuantity,
                        valueAmount = newAmount,
                    ),
                )
            } else {
                current
            }
        }
    }

    fun actionDismissCardDetails() {
        _entryListDisplayingStates.update { current ->
            if (current is EntryListDisplayingStates.Success) {
                current.copy(
                    data = current.data.copy(
                        showCardItemBottomSheet = false,
                    ),
                )
            } else {
                current
            }
        }
    }

    fun actionShowDeleteEntryConfirmation(isShown: Boolean) {
        _entryListDisplayingStates.update { current ->
            if (current is EntryListDisplayingStates.Success) {
                current.copy(
                    data = current.data.copy(
                        entryDeletionConfirmationDialogShown = isShown,
                    ),
                )
            } else {
                current
            }
        }
    }

    fun actionDeleteEntry(entryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteEntryUseCase(entryId)
        }
    }

    fun actionEditEntryInDB() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_entryListDisplayingStates.value is EntryListDisplayingStates.Success) {
                editDatabaseEntryUseCase(
                    sugarEntryID = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.entryInCardItem.id,
                    sugarEntryType = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.entryInCardItem.entryType,
                    newCategory = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.valueCategory,
                    newGramPerHundred = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.valueGramPerHundred.toDoubleFormattedOrNull(),
                    newGramPerPiece = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.valueGramPerPiece.toDoubleFormattedOrNull(),
                    oldCategory = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.entryInCardItem.category,
                    newQuantity = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.valueQuantity.toDoubleFormattedOrNull(),
                    newAmount = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.valueAmount.toDoubleFormattedOrNull(),
                )
            }
        }
    }

    fun actionReuseEntryForToday() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_entryListDisplayingStates.value is EntryListDisplayingStates.Success) {
                reuseEntryForTodayUseCase(
                    entrySugar = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.entryInCardItem,
                )
            }
        }
    }

    fun actionChangeSearchFieldShown() {
        _entryListDisplayingStates.update { current ->
            if (current is EntryListDisplayingStates.Success) {
                current.copy(
                    data = current.data.copy(
                        searchFieldShown = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.searchFieldShown.not(),
                    ),
                )
            } else {
                current
            }
        }
    }

    fun actionChangeSearchTextFieldText(newText: String) {
        _entryListDisplayingStates.update { current ->
            if (current is EntryListDisplayingStates.Success) {
                current.copy(
                    data = current.data.copy(
                        searchFieldText = newText,
                    ),
                )
            } else {
                current
            }
        }
    }

    fun actionFilterEntryListInHistory() {
        if (_entryListDisplayingStates.value is EntryListDisplayingStates.Success) {
            val filteredEntryList = filterEntriesBySearchFieldUseCase(
                searchFieldText = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.searchFieldText,
                entryList = (_entryListDisplayingStates.value as EntryListDisplayingStates.Success).data.entriesGroupedPerDayUnfilteredHistory,
            )
            _entryListDisplayingStates.update { current ->
                if (current is EntryListDisplayingStates.Success) {
                    current.copy(
                        data = current.data.copy(
                            entriesGroupedPerDayHistory = filteredEntryList,
                        ),
                    )
                } else {
                    current
                }
            }
        }
    }

    fun actionCloseSearchFieldAndClearText() {
        _entryListDisplayingStates.update { current ->
            if (current is EntryListDisplayingStates.Success) {
                current.copy(
                    data = current.data.copy(
                        searchFieldShown = false,
                        searchFieldText = "",
                        entriesGroupedPerDayHistory = current.data.entriesGroupedPerDayUnfilteredHistory,
                    ),
                )
            } else {
                current
            }
        }
    }
}
