package com.example.mva_sugarcounter.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mva_sugarcounter.data.Entry
import com.example.mva_sugarcounter.database.AppDatabase
import com.example.mva_sugarcounter.util.HelperMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SettingsVM(private val application: Application) : AndroidViewModel(application) {

    val helperMethods: HelperMethods = HelperMethods(application)
    private val database = AppDatabase.getInstance(this.getApplication())

    //SateFlows: START
    val _settingsScreenShown = MutableStateFlow(true)
    val settingsScreenShown = _settingsScreenShown.asStateFlow()
    //SateFlows: END

    //Actions: START
    fun actionShowSettingsScreen() {
        _settingsScreenShown.value = true
    }

    fun actionHideSettingsScreen() {
        _settingsScreenShown.value = false
    }

    fun actionShowThirdPartyLicenses() {

    }

    fun actionHideThirdPartyLicenses() {

    }

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
                        date = helperMethods.formatDateToString(
                            timestamp,
                            "YYYY-MM-dd"
                        ),
                        gramItem = gramValue,
                        amount = 1,
                        category = "Test",
                        gramTotal = gramValue
                    )
                )
            }
        }
    }
    //Testing Purposes: END

    //Actions: END

}