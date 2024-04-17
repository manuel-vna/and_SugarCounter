package com.example.mva_sugarcounter.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsVM(private val application: Application) : AndroidViewModel(application) {

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
    //Actions: END


}