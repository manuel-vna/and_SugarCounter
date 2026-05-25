package com.jumparoundcreations.mva_sugarcounter.features.settingsFeature

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.BottomSheetsSettings
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.settingsFeature.useCases.ExportEntriesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsVM(
    val exportEntriesUseCase: ExportEntriesUseCase
) : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()
    private val sharedPrefsMain by inject<SharedPreferences>()

    private val _settingsStates = MutableStateFlow(
        SettingsStates(
            entriesDeletionActivated = loadShaPrefEntriesDeletionSwitch(),
            dynamicColorActivated = loadShaPrefColorSchemeSwitch(),
            deletionWorkerSlider = loadShaPrefEntriesDeletionSwitchValue()
        )
    )
    val settingsStates = _settingsStates.asStateFlow()

    private val _settingsEffects = MutableSharedFlow<SettingsEffect>(replay = 0)
    val settingsEffects = _settingsEffects.asSharedFlow()

    //Actions: START

    fun actionChangeExpandedId(id: Long) {
        _settingsStates.update { it.copy(faqExpandedId = id) }
    }

    fun actionUpdateGramThresholdSharedPref() {
        sharedPrefsMain.edit {
            putInt("gramThresholdValue", _settingsStates.value.gramThresholdSlider.toInt())
        }
    }

    fun actionUpdateGramThresholdSlider(sliderPosition: Float) {
        _settingsStates.update { it.copy(gramThresholdSlider = sliderPosition) }
    }

    fun actionGramThresholdDialogCheck(isShown: Boolean) {
        _settingsStates.update { it.copy(gramThresholdDialogCheck = isShown) }
    }

    fun actionResetThresholdSliderValuesToSharedPref() {
        _settingsStates.update {
            it.copy(
                gramThresholdSlider = sharedPrefsMain.getInt(
                    "gramThresholdValue",
                    50
                ).toFloat()
            )
        }
    }

    fun actionUpdateDeletionWorkerSlider(sliderPosition: Float) {
        _settingsStates.update { it.copy(deletionWorkerSlider = sliderPosition.toInt()) }
        sharedPrefsMain.edit {
            putInt("automaticDeletionValue", sliderPosition.toInt())
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun actionExportEntries(
        context: Context,
        osVersionHigherOrEqualsR: Boolean,
        settingsVM: SettingsVM
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            exportEntriesUseCase(
                context,
                osVersionHigherOrEqualsR,
                settingsVM,
                database
            )
        }
    }

    fun actionIncrementExportProgressIndicator() {
        _settingsStates.update { it.copy(exportProgressIndicator = it.exportProgressIndicator + 0.1f) }
        println("ProgressIndicator: ${_settingsStates.value.exportProgressIndicator}")
    }

    fun actionChangExportProgressIndicatorVisibility(isShown: Boolean) {
        _settingsStates.update { it.copy(exportProgressIndicatorShown = isShown) }
    }

    fun actionChangeExportBottomSheetVisibility(isShown: Boolean) {
        _settingsStates.update { it.copy(dataSuccessfullyExportedShown = isShown) }
    }

    fun actionChangeExportSuccessfully(wasSuccessful: Boolean) {
        _settingsStates.update { it.copy(exportSuccessfully = wasSuccessful) }
    }


    fun actionChangeEntriesDeletionActivated(isActivated: Boolean) {
        _settingsStates.update { it.copy(entriesDeletionActivated = isActivated) }

        sharedPrefsMain.edit {
            putBoolean("entriesDeletionActivated", isActivated)
        }
    }

    fun actionChangeDynamicColorActivated(isActivated: Boolean) {
        _settingsStates.update { it.copy(dynamicColorActivated = isActivated) }

        sharedPrefsMain.edit { putBoolean("dynamicColorActivated", isActivated) }
    }

    fun actionChangeBottomSheetsSetting(shownSheet: BottomSheetsSettings) {
        _settingsStates.update { it.copy(bottomSheetsSettings = shownSheet) }
    }

    //Actions: END

    // Effects START

    fun emitEffect(effect: SettingsEffect) {
        viewModelScope.launch {
            _settingsEffects.emit(effect)
        }
    }

    // Effects END

    // Loading Shared Preferences: START

    private fun loadShaPrefEntriesDeletionSwitch(): Boolean {
        return sharedPrefsMain.getBoolean(
            "entriesDeletionActivated",
            false
        )
    }

    private fun loadShaPrefEntriesDeletionSwitchValue(): Int {
        return sharedPrefsMain.getInt(
            "automaticDeletionValue",
            3
        )
    }

    private fun loadShaPrefColorSchemeSwitch(): Boolean {
        return sharedPrefsMain.getBoolean(
            "dynamicColorActivated",
            false
        )
    }


}