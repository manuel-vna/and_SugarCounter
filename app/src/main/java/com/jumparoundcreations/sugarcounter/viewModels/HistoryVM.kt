package com.jumparoundcreations.sugarcounter.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.sugarcounter.data.EntryGroup
import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.sugarcounter.util.DatabaseConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class HistoryVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()

    companion object {
        private const val INDEX_DEFAULT = 0
        private const val TIMEOUT = 5000L
    }

    //SateFlows: START

    private val _isCardTabIndex = MutableStateFlow(INDEX_DEFAULT)
    val isCardTabIndex = _isCardTabIndex.asStateFlow()

    private val _segmentedButtonSugarOrCaloriesIndex = MutableStateFlow(INDEX_DEFAULT)
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
                    SugarEntry(
                        DatabaseConstants.DEFAULT_DATABASE_INT,
                        DatabaseConstants.DEFAULT_DATABASE_TIMESTAMP,
                        DatabaseConstants.DEFAULT_DATABASE_STRING,
                        DatabaseConstants.DEFAULT_DATABASE_STRING,
                        GramCountMode.PerHundred,
                        DatabaseConstants.DEFAULT_DATABASE_DOUBLE,
                        DatabaseConstants.DEFAULT_DATABASE_DOUBLE,
                        DatabaseConstants.DEFAULT_DATABASE_DOUBLE,
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
            SharingStarted.WhileSubscribed(TIMEOUT),
            _savedHistory.value
        )

    //SateFlows: END


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
    //Actions: END
}
