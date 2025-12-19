package com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases.DeleteEntryUseCase
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases.EditDatabaseEntryUseCase
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases.FilterEntriesBySearchFieldUseCase
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases.GetEntryGroupPerDayUseCase
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases.ReuseEntryForTodayUseCase
import com.jumparoundcreations.sugarcounter.util.TimeConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class EntryListDisplayingViewModel(
    val getEntryGroupPerDayUseCase: GetEntryGroupPerDayUseCase,
    val deleteEntryUseCase: DeleteEntryUseCase,
    val editDatabaseEntryUseCase: EditDatabaseEntryUseCase,
    val reuseEntryForTodayUseCase: ReuseEntryForTodayUseCase,
    val filterEntriesBySearchFieldUseCase: FilterEntriesBySearchFieldUseCase
) : ViewModel(), KoinComponent {

    private val _entryListDisplayingStates = MutableStateFlow(EntryListDisplayingStates())
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
                actionEditGram(action.newGram)
            }

            is EntryListDisplayingIntents.EditQuantity -> {
                actionEditQuantity(action.newQuantity)
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

        }
    }

    init {
        groupEntriesPerDayCounter()
        groupEntriesPerDayHistory()
    }

    fun groupEntriesPerDayCounter() {
        viewModelScope.launch {
            getEntryGroupPerDayUseCase(
                timeFrameBeginning =
                    TimeConstants.ONE_DAY_IN_SECONDS
            ).collect { entryGroupListCounter ->
                _entryListDisplayingStates.update {
                    it.copy(
                        entriesGroupedPerDayCounter = entryGroupListCounter
                    )
                }
            }
        }
    }

    fun groupEntriesPerDayHistory() {
        viewModelScope.launch {
            getEntryGroupPerDayUseCase(
                timeFrameBeginning =
                    TimeConstants.NINETY_DAYS_IN_SECONDS
            ).collect { entryGroupListHistory ->
                _entryListDisplayingStates.update {
                    it.copy(
                        entriesGroupedPerDayHistory = entryGroupListHistory,
                        entriesGroupedPerDayUnfilteredHistory = entryGroupListHistory
                    )
                }
            }
        }
    }

    fun actionShowCardDetails(sugarEntry: SugarEntry) {

        _entryListDisplayingStates.update {
            it.copy(
                showCardItemBottomSheet = true
            )
        }

        _entryListDisplayingStates.update {
            it.copy(
                entryInCardItem = sugarEntry
            )
        }
    }

    fun actionLoadEntryDataIntoCardDetails(sugarEntry: SugarEntry) {
        _entryListDisplayingStates.update {
            it.copy(
                valueCategory = sugarEntry.category,
                valueGram = sugarEntry.gram.toString(),
                valueQuantity = sugarEntry.quantity.toString()
            )
        }
    }

    fun actionEditCategory(newCategory: String) {
        _entryListDisplayingStates.update {
            it.copy(
                valueCategory = newCategory
            )
        }
    }

    fun actionEditGram(newGram: String) {
        _entryListDisplayingStates.update {
            it.copy(
                valueGram = newGram
            )
        }
    }

    fun actionEditQuantity(newQuantity: String) {
        _entryListDisplayingStates.update {
            it.copy(
                valueQuantity = newQuantity
            )
        }
    }

    fun actionDismissCardDetails() {
        _entryListDisplayingStates.update {
            it.copy(
                showCardItemBottomSheet = false
            )
        }
    }

    fun actionShowDeleteEntryConfirmation(isShown: Boolean) {
        _entryListDisplayingStates.update {
            it.copy(
                entryDeletionConfirmationDialogShown = isShown
            )
        }
    }

    fun actionDeleteEntry(entryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteEntryUseCase(entryId)
        }
    }

    fun actionEditEntryInDB() {
        viewModelScope.launch(Dispatchers.IO) {
            editDatabaseEntryUseCase(
                sugarEntryID = _entryListDisplayingStates.value.entryInCardItem.id,
                sugarEntryType = _entryListDisplayingStates.value.entryInCardItem.entryType,
                newCategory = _entryListDisplayingStates.value.valueCategory,
                newGram = _entryListDisplayingStates.value.valueGram.toDouble(),
                oldCategory = _entryListDisplayingStates.value.entryInCardItem.category,
                newQuantity = _entryListDisplayingStates.value.valueQuantity.toDouble()
            )
        }
    }

    fun actionReuseEntryForToday() {
        viewModelScope.launch(Dispatchers.IO) {
            reuseEntryForTodayUseCase(
                entrySugar = _entryListDisplayingStates.value.entryInCardItem
            )
        }
    }


    fun actionChangeSearchFieldShown() {
        _entryListDisplayingStates.update {
            it.copy(
                searchFieldShown = _entryListDisplayingStates.value.searchFieldShown.not()
            )
        }
    }

    fun actionChangeSearchTextFieldText(newText: String) {
        _entryListDisplayingStates.update {
            it.copy(
                searchFieldText = newText
            )
        }
    }

    fun actionFilterEntryListInHistory() {
        val filteredEntryList = filterEntriesBySearchFieldUseCase(
            searchFieldText = _entryListDisplayingStates.value.searchFieldText,
            entryList = _entryListDisplayingStates.value.entriesGroupedPerDayUnfilteredHistory
        )
        _entryListDisplayingStates.update {
            it.copy(
                entriesGroupedPerDayHistory = filteredEntryList

            )
        }
    }

}