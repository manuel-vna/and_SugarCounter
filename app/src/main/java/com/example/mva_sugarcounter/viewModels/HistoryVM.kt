package com.example.mva_sugarcounter.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.example.mva_sugarcounter.data.Entry
import com.example.mva_sugarcounter.database.AppDatabase
import com.example.mva_sugarcounter.util.HelperMethods
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.ZoneId


class HistoryVM(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(this.getApplication())

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
    val _savedHistory = MutableStateFlow(emptyMap<Pair<String, String>, List<Entry>>())
    val savedHistory = _savedHistory.asStateFlow()
    val _historyInfoDialogShown = MutableStateFlow(false)
    val historyInfoDialogShown = _historyInfoDialogShown.asStateFlow()
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

    //Actions: END
}
