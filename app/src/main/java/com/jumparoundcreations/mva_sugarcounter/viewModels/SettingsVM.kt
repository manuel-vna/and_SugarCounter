package com.jumparoundcreations.mva_sugarcounter.viewModels

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.ExportData
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.random.Random

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

    val _dataSuccesfullyExportedShown = MutableStateFlow(false)
    val dataSuccesfullyExportedShown = _dataSuccesfullyExportedShown.asStateFlow()

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

        val currentTimestampLong = System.currentTimeMillis() / 1000
        val currentTimestamp = HelperMethods.formatDateToString(
            currentTimestampLong,
            "YYYY-MM-dd_hh:mm"
        )
        val fileName = "sugarCounter-export-$currentTimestamp.txt"

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
                ExportData.exportEntriesViaFileWriter(allEntries, fileName, settingsVM)
            }
            println("PermissionGranted, osVersionHigherOrEqualsR: $osVersionHigherOrEqualsR")
        }
    }

    fun actionChangeExportBottomSheetShown(isShown: Boolean) {
        _dataSuccesfullyExportedShown.value = isShown
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