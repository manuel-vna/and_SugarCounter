package com.jumparoundcreations.mva_sugarcounter.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent

class HistoryVM(
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel(),
    KoinComponent {
    companion object {
        private const val INDEX_KEY = "isCardTabIndex"
        private const val INDEX_DEFAULT = 0
    }

    // SateFlows: START

    private val _isCardTabIndex = MutableStateFlow(
        savedStateHandle.get<Int>(INDEX_KEY) ?: INDEX_DEFAULT
    )
    val isCardTabIndex = _isCardTabIndex.asStateFlow()

    private val _historyTabTwoShown = MutableStateFlow(_isCardTabIndex.value == 1)
    val historyTabTwoShown = _historyTabTwoShown.asStateFlow()

    private val _historyTabOneShown = MutableStateFlow(_isCardTabIndex.value == 0)
    val historyTabOneShown = _historyTabOneShown.asStateFlow()

    // SateFlows: END

    // Actions: START
    private fun actionShowTabTwoScreen() {
        _historyTabTwoShown.value = true
    }

    private fun actionHideTabTwoScreen() {
        _historyTabTwoShown.value = false
    }

    private fun actionShowTabOneScreen() {
        _historyTabOneShown.value = true
    }

    private fun actionHideTabOneScreen() {
        _historyTabOneShown.value = false
    }

    fun actionSetIsCardTabIndex(tabIndex: Int) {
        _isCardTabIndex.value = tabIndex
        savedStateHandle[INDEX_KEY] = tabIndex

        if (tabIndex == 0) {
            actionShowTabOneScreen()
            actionHideTabTwoScreen()
        } else {
            actionHideTabOneScreen()
            actionShowTabTwoScreen()
        }
    }

    // Actions: END
}
