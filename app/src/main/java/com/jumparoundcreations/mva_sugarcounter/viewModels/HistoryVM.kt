package com.jumparoundcreations.mva_sugarcounter.viewModels

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.ZoneId


class HistoryVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()

    //Timestamps: START
    private val today = LocalDate.now()
    private val endOfToday =
        today.plusDays(1).atStartOfDay(ZoneId.systemDefault())
            .toEpochSecond() - 1 //ToDo millisecond approach: * 1000 - 1
    private val currentTimestamp = System.currentTimeMillis() / 1000
    private val endOf30DaysAgo =
        currentTimestamp - 6048000 // 6048000000 = 60 days in milliseconds
    //Timestamps: END

    //SateFlows: START
    val _historyChartScreenShown = MutableStateFlow(true)
    val historyChartScreenShown = _historyChartScreenShown.asStateFlow()
    val _historyCardsScreenShown = MutableStateFlow(false)
    val historyCardsScreenShown = _historyCardsScreenShown.asStateFlow()
    val _historyInfoDialogShown = MutableStateFlow(false)
    val historyInfoDialogShown = _historyInfoDialogShown.asStateFlow()
    val _historyCardSearchFieldShown = MutableStateFlow(false)
    val historyCardSearchFieldShown = _historyCardSearchFieldShown.asStateFlow()
    val _historyCardSearchFieldText = MutableStateFlow("")
    val historyCardSearchFieldText = _historyCardSearchFieldText.asStateFlow()
    val _savedHistory = MutableStateFlow(
        listOf(
            EntryGroup(
                "", "",
                listOf(
                    Entry(
                        0, 0, "", "", true,
                        0, 0, 0, 0, 0
                    )
                )
            )
        )
    )
    val savedHistory =
        historyCardSearchFieldText.combine(_savedHistory) { searchField, entries ->
            entries.filter { entryGroup ->
                entryGroup.entryList.any { entry ->
                    entry.category.contains(_historyCardSearchFieldText.value)
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _savedHistory.value
        )
    //SateFlows: END

    //Observer: START
    private val historyObserver = Observer<List<Entry>> {
        val savedSugarCountGrouped = HelperMethods.groupCounterItemsInGroupsByDay(it)
        _savedHistory.value = savedSugarCountGrouped
    }

    // When this ViewModal is initialized, tell the above created observer what has to be observed and how long
    init {
        database.appDao().getEntries(endOf30DaysAgo, endOfToday).observeForever(historyObserver)
    }

    override fun onCleared() {
        super.onCleared()
        // Stop observing at Dao of RoomDB when this ViewModel is cleared
        database.appDao().getEntries(endOf30DaysAgo, endOfToday).removeObserver(historyObserver)
    }
    //Observer: END


    //Actions: START
    fun actionShowHistoryChartScreen() {
        _historyChartScreenShown.value = true
    }

    fun actionHideHistoryChartScreen() {
        _historyChartScreenShown.value = false
    }

    fun actionShowHistoryCardsScreen() {
        _historyCardsScreenShown.value = true
    }

    fun actionHideHistoryCardsScreen() {
        _historyCardsScreenShown.value = false
    }

    fun actionShowInfoBoxForHistoryScreen() {
        _historyInfoDialogShown.value = true
    }

    fun actionDismissInfoDialog() {
        _historyInfoDialogShown.value = false
    }

    fun actionChangeHistoryCardSearchFieldShown(isShown: Boolean) {
        _historyCardSearchFieldShown.value = isShown
    }

    fun actionChangeHistoryCardSearchFieldText(searchText: String) {
        _historyCardSearchFieldText.value = searchText
    }

    //Actions: END
}
