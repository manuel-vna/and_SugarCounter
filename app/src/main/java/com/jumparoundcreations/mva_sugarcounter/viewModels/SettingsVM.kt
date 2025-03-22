package com.jumparoundcreations.mva_sugarcounter.viewModels

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class SettingsVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()
    private val sharedPrefsMain by inject<SharedPreferences>()

    //SateFlows: START
    private val _faqExpandedId = MutableStateFlow(-1L)
    var faqSingleSelectMode = _faqExpandedId.asStateFlow()

    private val _gramThresholdSlider = MutableStateFlow(0F)
    val gramThresholdSlider = _gramThresholdSlider.asStateFlow()

    private val _caloriesThresholdSlider = MutableStateFlow(0F)
    val caloriesThresholdSlider = _caloriesThresholdSlider.asStateFlow()

    private val _gramThresholDialogCheck = MutableStateFlow(false)
    val gramThresholdDialogCheck = _gramThresholDialogCheck.asStateFlow()

    private val _exportProgressIndicator = MutableStateFlow(0.1f)
    val exportProgressIndicator = _exportProgressIndicator.asStateFlow()

    private val _exportProgressIndicatorShown = MutableStateFlow(false)
    val exportProgressIndicatorShown = _exportProgressIndicatorShown.asStateFlow()

    private val _dataPreExportBottomSheetShown = MutableStateFlow(false)
    val dataPreExportBottomSheetShown = _dataPreExportBottomSheetShown.asStateFlow()

    private val _dataSuccessfullyExportedShown = MutableStateFlow(false)
    val dataSuccessfullyExportedShown = _dataSuccessfullyExportedShown.asStateFlow()

    private val _exportSuccessfully = MutableStateFlow(true)
    val exportSuccessfully = _exportSuccessfully.asStateFlow()

    private val _caloriesCounterActivated = MutableStateFlow(loadShaPrefCaloriesCounterSwitch())
    val caloriesCounterActivated = _caloriesCounterActivated.asStateFlow()

    private val _entriesDeletionBottomSheetShown = MutableStateFlow(false)
    val entriesDeletionBottomSheetShown = _entriesDeletionBottomSheetShown.asStateFlow()

    private val _entriesDeletionActivated = MutableStateFlow(loadShaPrefEntriesDeletionSwitch())
    val entriesDeletionActivated = _entriesDeletionActivated.asStateFlow()

    private val _donationBottomSheetShown = MutableStateFlow(false)
    val donationBottomSheetShown = _donationBottomSheetShown.asStateFlow()

    private val _colorSchemeBottomSheetShown = MutableStateFlow(false)
    val colorSchemeBottomSheetShown = _colorSchemeBottomSheetShown.asStateFlow()

    private val _dynamicColorActivated = MutableStateFlow(loadShaPrefColorSchemeSwitch())
    val dynamicColorActivated = _dynamicColorActivated.asStateFlow()

    //SateFlows: END

    //Actions: START

    fun actionChangeExpandedId(id: Long) {
        _faqExpandedId.value = id
    }

    fun actionUpdateGramThresholdSharedPref() {
        val editorSharedPrefsMain = sharedPrefsMain.edit()
        editorSharedPrefsMain.putInt("gramThresholdValue", _gramThresholdSlider.value.toInt())
        editorSharedPrefsMain.apply()
    }

    fun actionUpdateCaloriesThresholdSharedPref() {
        val editorSharedPrefsMain = sharedPrefsMain.edit()
        editorSharedPrefsMain.putInt(
            "caloriesThresholdValue",
            _caloriesThresholdSlider.value.toInt()
        )
        editorSharedPrefsMain.apply()
    }

    fun actionUpdateGramThresholdSlider(sliderPosition: Float) {
        _gramThresholdSlider.value = sliderPosition
    }

    fun actionUpdateCaloriesThresholdSlider(sliderPosition: Float) {
        val sliderPositionRoundedToTen = (sliderPosition / 10).roundToInt() * 10
        _caloriesThresholdSlider.value = sliderPositionRoundedToTen.toFloat()
    }

    fun actionGramThresholdDialogCheck(isShown: Boolean) {
        _gramThresholDialogCheck.value = isShown
    }

    fun actionResetThresholdSliderValuesToSharedPref() {
        _gramThresholdSlider.value = sharedPrefsMain.getInt(
            "gramThresholdValue",
            50
        ).toFloat()
        _caloriesThresholdSlider.value = sharedPrefsMain.getInt(
            "caloriesThresholdValue",
            2250
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

        viewModelScope.launch(Dispatchers.IO) {

            val allEntriesSugar = database.appDao().getAllEntries()
            val allEntriesCalories = database.appDao().getAllEntriesCalories()

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
                // export calories entries for OS versions higher R
                val fileNameCalories = "caloriesCounter-$timestampString"
                ExportData.exportEntriesViaMediaStore(
                    context = context,
                    allEntries = allEntriesCalories,
                    fileName = fileNameCalories,
                    settingsVM = settingsVM,
                    header = "Date,Name,kcalTotal\n"
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
                // export calories entries
                val fileNameCalories = "caloriesCounter-$timestampString"
                ExportData.exportEntriesViaFileWriter(
                    allEntries = allEntriesCalories,
                    fileName = fileNameCalories,
                    settingsVM = settingsVM,
                    header = "Date,Name,kcalTotal\n"
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

    fun actionChangeDataPreExportBottomSheetShown(isShown: Boolean) {
        _dataPreExportBottomSheetShown.value = isShown
    }

    fun actionChangeExportBottomSheetVisibility(isShown: Boolean) {
        _dataSuccessfullyExportedShown.value = isShown
    }

    fun actionChangeExportSuccessfully(wasSuccessful: Boolean) {
        _exportSuccessfully.value = wasSuccessful
    }

    fun actionChangeCaloriesCounterGeneral(isActivated: Boolean) {
        _caloriesCounterActivated.value = isActivated

        //Save activation boolean in SharedPreferences
        val editorSharedPrefsMain = sharedPrefsMain.edit()
        editorSharedPrefsMain.putBoolean(
            "caloriesCounterActivated",
            _caloriesCounterActivated.value
        )
        editorSharedPrefsMain.apply()
    }

    fun actionChangeCaloriesCounterSwitch(isActivated: Boolean) {
        _caloriesCounterActivated.value = isActivated
    }

    fun actionChangeEntriesDeletionBottomSheetShown(isShown: Boolean) {
        _entriesDeletionBottomSheetShown.value = isShown
    }

    fun actionChangeEntriesDeletionActivated(isActivated: Boolean) {
        _entriesDeletionActivated.value = isActivated

        val editorSharedPrefsMain = sharedPrefsMain.edit()
        editorSharedPrefsMain.putBoolean("entriesDeletionActivated", isActivated)
        editorSharedPrefsMain.apply()
    }

    fun actionChangeDonationBottomSheetShown(isShown: Boolean) {
        _donationBottomSheetShown.value = isShown
    }

    fun actionChangeColorSchemeBottomSheetShown(isShown: Boolean) {
        _colorSchemeBottomSheetShown.value = isShown
    }

    fun actionChangeDynamicColorActivated(isActivated: Boolean) {
        _dynamicColorActivated.value = isActivated

        sharedPrefsMain.edit { putBoolean("dynamicColorActivated", isActivated) }
    }

    //Actions: END

    // Loading Shared Preferences: START
    fun loadShaPrefCaloriesCounterSwitch(): Boolean {
        return sharedPrefsMain.getBoolean(
            "caloriesCounterActivated",
            false
        )
    }

    private fun loadShaPrefEntriesDeletionSwitch(): Boolean {
        return sharedPrefsMain.getBoolean(
            "entriesDeletionActivated",
            false
        )
    }

    private fun loadShaPrefColorSchemeSwitch(): Boolean {
        return sharedPrefsMain.getBoolean(
            "dynamicColorActivated",
            false
        )
    }
    // Loading Shared Preferences: END


}