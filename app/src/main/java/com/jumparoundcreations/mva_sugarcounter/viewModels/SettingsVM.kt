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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()
    private val sharedPrefsMain by inject<SharedPreferences>()

    //SateFlows: START
    private val _faqExpandedId = MutableStateFlow(-1L)
    var faqExpandedId = _faqExpandedId.asStateFlow()

    private val _gramThresholdSlider = MutableStateFlow(0F)
    val gramThresholdSlider = _gramThresholdSlider.asStateFlow()

    private val _gramThresholdDialogCheck = MutableStateFlow(false)
    val gramThresholdDialogCheck = _gramThresholdDialogCheck.asStateFlow()

    private val _exportProgressIndicator = MutableStateFlow(0.1f)
    val exportProgressIndicator = _exportProgressIndicator.asStateFlow()

    private val _exportProgressIndicatorShown = MutableStateFlow(false)
    val exportProgressIndicatorShown = _exportProgressIndicatorShown.asStateFlow()

    private val _dataSuccessfullyExportedShown = MutableStateFlow(false)
    val dataSuccessfullyExportedShown = _dataSuccessfullyExportedShown.asStateFlow()

    private val _exportSuccessfully = MutableStateFlow(true)
    val exportSuccessfully = _exportSuccessfully.asStateFlow()

    private val _entriesDeletionActivated = MutableStateFlow(loadShaPrefEntriesDeletionSwitch())
    val entriesDeletionActivated = _entriesDeletionActivated.asStateFlow()

    private val _dynamicColorActivated = MutableStateFlow(loadShaPrefColorSchemeSwitch())
    val dynamicColorActivated = _dynamicColorActivated.asStateFlow()

    private val _bottomSheetsSettings = MutableStateFlow(BottomSheetsSettings.NONE)
    val bottomSheetsSettings = _bottomSheetsSettings.asStateFlow()

    private val _deletionWorkerSlider = MutableStateFlow(loadShaPrefEntriesDeletionSwitchValue())
    val deletionWorkerSlider = _deletionWorkerSlider.asStateFlow()
    //SateFlows: END

    //Actions: START

    fun actionChangeExpandedId(id: Long) {
        _faqExpandedId.value = id
    }

    fun actionUpdateGramThresholdSharedPref() {
        sharedPrefsMain.edit {
            putInt("gramThresholdValue", _gramThresholdSlider.value.toInt())
        }
    }

    fun actionUpdateGramThresholdSlider(sliderPosition: Float) {
        _gramThresholdSlider.value = sliderPosition
    }

    fun actionGramThresholdDialogCheck(isShown: Boolean) {
        _gramThresholdDialogCheck.value = isShown
    }

    fun actionResetThresholdSliderValuesToSharedPref() {
        _gramThresholdSlider.value = sharedPrefsMain.getInt(
            "gramThresholdValue",
            50
        ).toFloat()
    }

    fun actionUpdateDeletionWorkerSlider(sliderPosition: Float) {
        _deletionWorkerSlider.value = sliderPosition.toInt()
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
        _exportProgressIndicator.value += 0.1f
        println("ProgressIndicator: ${exportProgressIndicator.value}")
    }

    fun actionChangExportProgressIndicatorVisibility(isShown: Boolean) {
        _exportProgressIndicatorShown.value = isShown
    }

    fun actionChangeExportBottomSheetVisibility(isShown: Boolean) {
        _dataSuccessfullyExportedShown.value = isShown
    }

    fun actionChangeExportSuccessfully(wasSuccessful: Boolean) {
        _exportSuccessfully.value = wasSuccessful
    }


    fun actionChangeEntriesDeletionActivated(isActivated: Boolean) {
        _entriesDeletionActivated.value = isActivated

        sharedPrefsMain.edit {
            putBoolean("entriesDeletionActivated", isActivated)
        }
    }

    fun actionChangeDynamicColorActivated(isActivated: Boolean) {
        _dynamicColorActivated.value = isActivated

        sharedPrefsMain.edit { putBoolean("dynamicColorActivated", isActivated) }
    }

    fun actionChangeBottomSheetsSetting(shownSheet: BottomSheetsSettings) {
        _bottomSheetsSettings.value = shownSheet
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