package com.jumparoundcreations.mva_sugarcounter.viewModels

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
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
    private val endOfXDaysAgo =
        currentTimestamp - 7776000 // 7776000 = 90 days in seconds
    //Timestamps: END

    //SateFlows: START

    private val _isCardTabIndex = MutableStateFlow(0)
    val isCardTabIndex = _isCardTabIndex.asStateFlow()

    private val _segmentedButtonSugarOrCaloriesIndex = MutableStateFlow(0)
    val segmentedButtonSugarOrCaloriesIndex = _segmentedButtonSugarOrCaloriesIndex.asStateFlow()

    private val _historyChartScreenShown = MutableStateFlow(false)
    val historyChartScreenShown = _historyChartScreenShown.asStateFlow()

    private val _historyCardsScreenShown = MutableStateFlow(true)
    val historyCardsScreenShown = _historyCardsScreenShown.asStateFlow()

    private val _historyCardSearchFieldShown = MutableStateFlow(false)
    val historyCardSearchFieldShown = _historyCardSearchFieldShown.asStateFlow()

    private val _historyCardSearchFieldText = MutableStateFlow("")
    val historyCardSearchFieldText = _historyCardSearchFieldText.asStateFlow()

    private val _savedHistory = MutableStateFlow(
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
    // fun <T1, T2, R> Flow<T1>.combine(flow: Flow<T2>, transform: suspend (a: T1, b: T2) -> R):
    // Returns a Flow whose values are generated with transform function by combining the most
        // recently emitted values by each flow.
        historyCardSearchFieldText.combine(_savedHistory) { searchField, entries ->
            entries.filter { entryGroup ->
                entryGroup.entryList.any { entry ->
                    entry.category.contains(_historyCardSearchFieldText.value)
                }
            }
        }.stateIn( // Converts a cold Flow into a hot StateFlow (= return value)
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _savedHistory.value
        )

    private val _caloriesEntryDbHistory = MutableStateFlow(
        listOf(
            EntryGroup(
                "", "",
                listOf(
                    EntryCalories(
                        0, 0, "", "", 0, 1, 0
                    )
                )
            )
        )
    )
    val caloriesEntryDbHistory =
    // fun <T1, T2, R> Flow<T1>.combine(flow: Flow<T2>, transform: suspend (a: T1, b: T2) -> R):
    // Returns a Flow whose values are generated with transform function by combining the most
        // recently emitted values by each flow.
        historyCardSearchFieldText.combine(_caloriesEntryDbHistory) { searchField, entries ->
            entries.filter { entryGroup ->
                entryGroup.entryList.any { entry ->
                    entry.category.contains(_historyCardSearchFieldText.value)
                }
            }
        }.stateIn( // Converts a cold Flow into a hot StateFlow (= return value)
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _caloriesEntryDbHistory.value
        )

    private var _segmentedButtonIndex = MutableStateFlow(0)
    val segmentedButtonIndex = _segmentedButtonIndex.asStateFlow()

    //SateFlows: END

    //Observer: START
    private val historyObserver = Observer<List<Entry>> {
        val savedSugarCountGrouped = HelperMethods.groupCounterItemsInGroupsByDay(it)
        _savedHistory.value = savedSugarCountGrouped
    }

    private val caloriesHistoryObserver = Observer<List<EntryCalories>> {
        val _caloriesEntryDbHistoryGrouped = HelperMethods.groupCounterItemsInGroupsByDay(it)
        _caloriesEntryDbHistory.value = _caloriesEntryDbHistoryGrouped
    }

    // When this ViewModal is initialized, tell the above created observer what has to be observed and how long
    init {
        database.appDao().getEntries(endOfXDaysAgo, endOfToday).observeForever(historyObserver)
        database.appDao().getEntryCalories(endOfXDaysAgo, endOfToday)
            .observeForever(caloriesHistoryObserver)
    }

    override fun onCleared() {
        super.onCleared()
        // Stop observing at Dao of RoomDB when this ViewModel is cleared
        database.appDao().getEntries(endOfXDaysAgo, endOfToday).removeObserver(historyObserver)
        database.appDao().getEntryCalories(endOfXDaysAgo, endOfToday)
            .removeObserver(caloriesHistoryObserver)

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

    fun actionSetIsCardTabIndex(tabIndex: Int) {
        _isCardTabIndex.value = tabIndex
    }

    fun actionChangeSegmentedButtonSugarOrCaloriesIndex(index: Int) {
        _segmentedButtonSugarOrCaloriesIndex.value = index
    }

    fun actionChangeHistoryCardSearchFieldShown(isShown: Boolean) {
        _historyCardSearchFieldShown.value = isShown
    }

    fun actionChangeHistoryCardSearchFieldText(searchText: String) {
        _historyCardSearchFieldText.value = searchText
    }

    fun actionChangeSegmentedButtonIndex(index: Int) {
        _segmentedButtonIndex.value = index
    }
    //Actions: END
}
