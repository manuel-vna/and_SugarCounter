package com.jumparoundcreations.sugarcounter.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent


class HistoryVM : ViewModel(), KoinComponent {

    companion object {
        private const val INDEX_DEFAULT = 0
    }

    //SateFlows: START

    private val _isCardTabIndex = MutableStateFlow(INDEX_DEFAULT)
    val isCardTabIndex = _isCardTabIndex.asStateFlow()

    private val _historyChartScreenShown = MutableStateFlow(false)
    val historyChartScreenShown = _historyChartScreenShown.asStateFlow()

    private val _historyCardsScreenShown = MutableStateFlow(true)
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

    fun actionSetIsCardTabIndex(tabIndex: Int) {
        _isCardTabIndex.value = tabIndex
    }

    //Actions: END
}
