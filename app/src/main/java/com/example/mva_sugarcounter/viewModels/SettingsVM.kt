package com.example.mva_sugarcounter.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mva_sugarcounter.data.Entry
import com.example.mva_sugarcounter.database.AppDatabase
import com.example.mva_sugarcounter.util.HelperMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

class SettingsVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()

    //SateFlows: START
    val _settingsScreenShown = MutableStateFlow(true)
    val settingsScreenShown = _settingsScreenShown.asStateFlow()

    val _faqScreenShown = MutableStateFlow(false)
    val faqScreenShown = _faqScreenShown.asStateFlow()

    val _faqExpandedId = MutableStateFlow(-1L)
    var faqSingleSelectMode = _faqExpandedId.asStateFlow()

    //SateFlows: END

    //Actions: START
    fun actionShowSettingsScreen() {
        _settingsScreenShown.value = true
    }

    fun actionHideSettingsScreen() {
        _settingsScreenShown.value = false
    }

    fun actionShowFaqScreen() {
        _faqScreenShown.value = true
    }

    fun actionHideFaqScreen() {
        _faqScreenShown.value = false
    }

    fun actionChangeExpandedId(id: Long) {
        _faqExpandedId.value = id
    }
    //Actions: END


    //Testing Purposes: START
    fun actionAddTestData() {
        viewModelScope.launch(Dispatchers.IO) {
            var timestamp = 1713643692000
            repeat(40) {
                timestamp += 86400000
                var gramValue = Random.nextInt(from = 1, until = 61)

                database.appDao().insertEntry(
                    Entry(
                        currentTimestamp = timestamp,
                        date = HelperMethods.formatDateToString(
                            timestamp,
                            "YYYY-MM-dd"
                        ),
                        category = "Test",
                        isPerHundred = true,
                        perPieceGram = gramValue,
                        perPieceAmount = 1,
                        perHundredGram = 0,
                        perHundredQuantity = 0,
                        gramTotal = gramValue
                    )
                )
            }
        }
    }
    //Testing Purposes: END

}