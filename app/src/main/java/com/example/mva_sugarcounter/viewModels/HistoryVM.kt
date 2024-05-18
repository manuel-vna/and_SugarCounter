package com.example.mva_sugarcounter.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class HistoryVM(application: Application) : AndroidViewModel(application) {

    //SateFlows: START
    val _historyChartScreenShown = MutableStateFlow(false)
    val historyChartScreenShown = _historyChartScreenShown.asStateFlow()
    val _historyCardsScreenShown = MutableStateFlow(false)
    val historyCardsScreenShown = _historyCardsScreenShown.asStateFlow()
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
    //Actions: END
}