package com.jumparoundcreations.mva_sugarcounter.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent

class HistoryVM :
    ViewModel(),
    KoinComponent {
    companion object {
        private const val INDEX_DEFAULT = 0
    }

    // SateFlows: START

    private val _isCardTabIndex = MutableStateFlow(INDEX_DEFAULT)
    val isCardTabIndex = _isCardTabIndex.asStateFlow()

    private val _historyTabTwoShown = MutableStateFlow(false)
    val historyTabTwoShown = _historyTabTwoShown.asStateFlow()

    private val _historyTabOneShown = MutableStateFlow(true)
    val historyTabOneShown = _historyTabOneShown.asStateFlow()

    // SateFlows: END

    // Actions: START
    fun actionShowTabTwoScreen() {
        _historyTabTwoShown.value = true
    }

    fun actionHideTabTwoScreen() {
        _historyTabTwoShown.value = false
    }

    fun actionShowTabOneScreen() {
        _historyTabOneShown.value = true
    }

    fun actionHideTabOneScreen() {
        _historyTabOneShown.value = false
    }

    fun actionSetIsCardTabIndex(tabIndex: Int) {
        _isCardTabIndex.value = tabIndex
    }

    // Actions: END
}
