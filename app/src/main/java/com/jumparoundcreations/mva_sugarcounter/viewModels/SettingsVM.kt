package com.jumparoundcreations.mva_sugarcounter.viewModels

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.BottomSheetsSettings
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.ExportData
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.settingsFeature.SettingsStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()
    private val sharedPrefsMain by inject<SharedPreferences>()

    //StateFlow: START
    private val _settingsStates = MutableStateFlow(
        SettingsStates(
            entriesDeletionActivated = loadShaPrefEntriesDeletionSwitch(),
            dynamicColorActivated = loadShaPrefColorSchemeSwitch(),
            deletionWorkerSlider = loadShaPrefEntriesDeletionSwitchValue()
        )
    )
    val settingsStates = _settingsStates.asStateFlow()
    //StateFlow: END

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

        val timestamp = System.currentTimeMillis()
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault())
        val timestampString = sdf.format(date)

        viewModelScope.launch(Dispatchers.IO) {

            val allEntriesSugar = database.appDao().getAllEntries()

            if (osVersionHigherOrEqualsR) {

                // export sugar entries for OS versions higher R
                val fileNameSugar = "sugarCounter-$timestampString"
                ExportData.exportEntriesViaMediaStore(
                    context = context,
                    allEntries = allEntriesSugar,
                    fileName = fileNameSugar,
                    settingsVM = settingsVM,
                    header = "Date,Name,Mode,Gram perHundred/perPiece,QuantityGram/AmountNumber,GramTotal\n"
                )

            } else {

                // export sugar entries
                val fileNameSugar = "sugarCounter-$timestampString"
                ExportData.exportEntriesViaFileWriter(
                    allEntries = allEntriesSugar,
                    fileName = fileNameSugar,
                    settingsVM = settingsVM,
                    header = "Date,Name,Mode,Gram perHundred/perPiece,QuantityGram/AmountNumber,GramTotal\n"
                )
            }
            println("PermissionGranted, osVersionHigherOrEqualsR: $osVersionHigherOrEqualsR")
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