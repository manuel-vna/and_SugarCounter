package com.jumparoundcreations.mva_sugarcounter.viewModels

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.data.ExportData
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()
    private val sharedPrefsMain by inject<SharedPreferences>(qualifier = named("sharedPrefsMain"))

    //SateFlows: START
    val _settingsScreenShown = MutableStateFlow(true)
    val settingsScreenShown = _settingsScreenShown.asStateFlow()

    val _faqScreenShown = MutableStateFlow(false)
    val faqScreenShown = _faqScreenShown.asStateFlow()

    val _faqExpandedId = MutableStateFlow(-1L)
    var faqSingleSelectMode = _faqExpandedId.asStateFlow()

    val _gramThresholdSlider = MutableStateFlow(0F)
    val gramThresholdSlider = _gramThresholdSlider.asStateFlow()

    val _gramThresholDialogCheck = MutableStateFlow(false)
    val gramThresholdDialogCheck = _gramThresholDialogCheck.asStateFlow()

    val _exportProgressIndicator = MutableStateFlow(0.1f)
    val exportProgressIndicator = _exportProgressIndicator.asStateFlow()

    val _exportProgressIndicatorShown = MutableStateFlow(false)
    val exportProgressIndicatorShown = _exportProgressIndicatorShown.asStateFlow()

    val _dataSuccesfullyExportedShown = MutableStateFlow(false)
    val dataSuccesfullyExportedShown = _dataSuccesfullyExportedShown.asStateFlow()

    val _exportSuccessfully = MutableStateFlow(true)
    val exportSuccessfully = _exportSuccessfully.asStateFlow()

    //SateFlows: END

    //Actions: START

    fun actionChangeSettingsScreenVisibility(isShown: Boolean) {
        _settingsScreenShown.value = isShown
    }

    fun actionChangeFaqScreenVisibility(isShown: Boolean) {
        _faqScreenShown.value = isShown
    }

    fun actionChangeExpandedId(id: Long) {
        _faqExpandedId.value = id
    }

    fun actionUpdateGramThresholdSharedPref() {
        val editorSharedPrefsMain = sharedPrefsMain.edit()
        editorSharedPrefsMain.putInt("gramThresholdValue", _gramThresholdSlider.value.toInt())
        editorSharedPrefsMain.apply()
    }

    fun actionUpdateGramThresholdSlider(sliderPosition: Float) {
        _gramThresholdSlider.value = sliderPosition
    }

    fun actionGramThresholdDialogCheck(isShown: Boolean) {
        _gramThresholDialogCheck.value = isShown
    }

    fun actionResetGramThresholdSliderToSharedPref() {
        _gramThresholdSlider.value = sharedPrefsMain.getInt(
            "gramThresholdValue",
            50
        ).toFloat()
    }

    @TargetApi(Build.VERSION_CODES.R)
    fun actionExportEntries(
        context: Context,
        osVersionHigherOrEqualsR: Boolean,
        settingsVM: SettingsVM
    ) {

        val timestamp = System.currentTimeMillis()
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault())
        val timestampString = sdf.format(date)
        val fileName = "sugarCounter-$timestampString.txt"

        viewModelScope.launch(Dispatchers.IO) {

            val allEntries = database.appDao().getAllEntries()

            if (osVersionHigherOrEqualsR) {
                ExportData.exportEntriesViaMediaStore(
                    context = context,
                    allEntries = allEntries,
                    fileName = fileName,
                    settingsVM = settingsVM
                )
            } else {
                ExportData.exportEntriesViaFileWriter(
                    allEntries = allEntries,
                    fileName = fileName,
                    settingsVM = settingsVM
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
        _dataSuccesfullyExportedShown.value = isShown
    }

    fun actionChangeExportSuccessfully(wasSuccessful: Boolean) {
        _exportSuccessfully.value = wasSuccessful
    }
    //Actions: END

}