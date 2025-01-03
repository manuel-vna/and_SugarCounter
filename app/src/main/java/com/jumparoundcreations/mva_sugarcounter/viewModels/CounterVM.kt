package com.jumparoundcreations.mva_sugarcounter.viewModels

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.jumparoundcreations.mva_sugarcounter.data.Category
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.data.IEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.util.CounterCaloriesHelper
import com.jumparoundcreations.mva_sugarcounter.util.CounterSugarHelper
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.time.LocalDate
import java.time.ZoneId


class CounterVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()
    private val sharedPrefsMain by inject<SharedPreferences>(qualifier = named("sharedPrefsMain"))
    private val barcodeScanner by inject<GmsBarcodeScanner>(qualifier = named("barcodeScanner"))

    // Timestamps: BEGIN
    private val epochTimestampSecondsNow = System.currentTimeMillis() / 1000
    private val today = LocalDate.now()
    private val startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
    private val startOfYesterday = startOfToday - 86400 // 86400 = 1 day in seconds
    private val endOfToday =
        today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() - 1
    // Timestamps: END

    //StateFlow: START

    private val _sugarEntryDbRecent = MutableStateFlow(
        listOf(
            EntryGroup<Entry>("", "", listOf())
        )
    )
    val sugarEntryDbRecent = _sugarEntryDbRecent.asStateFlow()

    private val _caloriesEntryDbRecent = MutableStateFlow(
        listOf(
            EntryGroup<EntryCalories>("", "", listOf())
        )
    )
    val caloriesEntryDbRecent = _caloriesEntryDbRecent.asStateFlow()

    private val _datePickerShown = MutableStateFlow(false)
    val datePickerShown = _datePickerShown.asStateFlow()

    private val _dateOfEntryEpochSec = MutableStateFlow(epochTimestampSecondsNow)
    val dateOfEntryEpochSec = _dateOfEntryEpochSec.asStateFlow()

    private val _categorySelected = MutableStateFlow("")
    val categorySelected = _categorySelected.asStateFlow()

    private val _categoryFieldExpanded = MutableStateFlow(false)
    val categoryFieldExpanded = _categoryFieldExpanded.asStateFlow()

    private val _categoryFieldSize = MutableStateFlow(Size.Zero)
    val categoryFieldSize = _categoryFieldSize.asStateFlow()

    private val _categories = MutableStateFlow(listOf<String>())
    val categories = _categories.asStateFlow()

    private val _isHundredTabIndex = MutableStateFlow(0)
    val isHundredTabIndex = _isHundredTabIndex.asStateFlow()

    private var _gramCountMode = MutableStateFlow(GramCountMode.PerHundred)
    var gramCountMode = _gramCountMode.asStateFlow()

    private var _perPieceGram = MutableStateFlow("")
    val perPieceGram = _perPieceGram.asStateFlow()

    private var _perPieceAmount = MutableStateFlow("")
    val perPieceAmount = _perPieceAmount.asStateFlow()

    private var _perHundredGram = MutableStateFlow("")
    val perHundredGram = _perHundredGram.asStateFlow()

    private var _perHundredQuantity = MutableStateFlow("")
    val perHundredQuantity = _perHundredQuantity.asStateFlow()

    private val _alertDialog = MutableStateFlow(false)
    val alertDialog = _alertDialog.asStateFlow()

    private val _noDataForChosenCategorySnackbarShown = MutableStateFlow(false)
    val noDataForChosenCategorySnackbarShown = _noDataForChosenCategorySnackbarShown.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog = _showDeleteDialog.asStateFlow()

    private val _itemToDeleteIsEntrySugar = MutableStateFlow(false)
    val itemToDeleteIsEntrySugar = _itemToDeleteIsEntrySugar.asStateFlow()


    private val _categoryItemDeleteObject = MutableStateFlow(
        Entry(
            0, 0, "", "", true,
            0, 0, 0, 0, 0
        )
    )
    val categoryItemDeleteObject = _categoryItemDeleteObject.asStateFlow()

    private val _itemToDeleteEntrySugar = MutableStateFlow(
        Entry(
            0, 0, "", "", true,
            0, 0, 0, 0, 0
        )
    )
    val itemToDeleteEntrySugar = _itemToDeleteEntrySugar.asStateFlow()

    private val _itemToDeleteEntryCalories = MutableStateFlow(
        EntryCalories(
            0, 0, "", "", 0
        )
    )
    val itemToDeleteEntryCalories = _itemToDeleteEntryCalories.asStateFlow()

    val _alertDialogGramThreshold = MutableStateFlow(false)
    val alertDialogGramThreshold = _alertDialogGramThreshold.asStateFlow()

    val _alertCaloriesThreshold = MutableStateFlow(false)
    val alertCaloriesThreshold = _alertCaloriesThreshold.asStateFlow()

    private val _noBarcodeYetInfoTitle = MutableStateFlow(false)
    val noBarcodeYetInfoTitle = _noBarcodeYetInfoTitle.asStateFlow()

    private val _noBarcodeYetInfoDescription = MutableStateFlow(false)
    val noBarcodeYetInfoDescription = _noBarcodeYetInfoDescription.asStateFlow()

    private val _barcodeNumber = MutableStateFlow("")
    val barcodeNumber = _barcodeNumber.asStateFlow()

    private var _caloriesInput = MutableStateFlow("")
    val caloriesInput = _caloriesInput.asStateFlow()

    private var _segmentedButtonIndex = MutableStateFlow(0)
    val segmentedButtonIndex = _segmentedButtonIndex.asStateFlow()
    //StateFlow: END


    // Observer that is used to observe a method in the Dao which fetches a list of Category items
    private val categoryObserver = Observer<List<Category>> {
        _categories.value = it.map { it.category }
    }

    // Observer that is used to observe a method in the Dao which fetches a list of Entry items
    private val todayObserverObject = Observer<List<Entry>> {
        val savedSugarCountGrouped = HelperMethods.groupCounterItemsInGroupsByDay(it)
        _sugarEntryDbRecent.value = savedSugarCountGrouped
    }

    //Observer that is used to observe a method in the Dao which fetches a list of EntryCalories
    private val caloriesTodayObserverObject = Observer<List<EntryCalories>> {
        val caloriesTodayObserverObjectGrouped = HelperMethods.groupCounterItemsInGroupsByDay(it)
        _caloriesEntryDbRecent.value = caloriesTodayObserverObjectGrouped
        print(caloriesTodayObserverObjectGrouped)
    }

    // When this ViewModal is initialized, tell the above created observer what has to be observed and how long
    init {
        database.appDao().getAllCategories().observeForever(categoryObserver)

        database.appDao().getEntries(startOfYesterday, endOfToday)
            .observeForever(todayObserverObject)

        database.appDao().getEntryCalories(startOfYesterday, endOfToday)
            .observeForever(caloriesTodayObserverObject)

    }

    override fun onCleared() {
        super.onCleared()
        // Stop observing at Dao of RoomDB when this ViewModel is cleared
        database.appDao().getAllCategories().removeObserver(categoryObserver)

        database.appDao().getEntries(startOfYesterday, endOfToday)
            .removeObserver(todayObserverObject)

        database.appDao().getEntryCalories(startOfYesterday, endOfToday)
            .removeObserver(caloriesTodayObserverObject)
    }

    //Saving an Entry: Start
    fun saveEntry(category: String) {
        if (_perPieceGram.value.isNotEmpty() || _perHundredGram.value.isNotEmpty()) {
            CounterSugarHelper.saveSugarEntryInDatabase(
                viewModelScope = viewModelScope,
                sharedPrefsMain,
                counterVM = this,
                category = category,
                dateOfEntryEpochSecValue = _dateOfEntryEpochSec.value,
                gramCountModeValue = _gramCountMode.value,
                perHundredGramValue = _perHundredGram.value,
                perHundredQuantityValue = _perHundredQuantity.value,
                perPieceGramValue = _perPieceGram.value,
                perPieceAmountValue = _perPieceAmount.value
            )
        }

        if (_caloriesInput.value.isNotEmpty()) {
            CounterCaloriesHelper.saveCaloriesEntryInDatabase(
                sharedPreferences = sharedPrefsMain,
                viewModelScope = viewModelScope,
                counterVM = this,
                category = category,
                dateOfEntryEpochSecValue = _dateOfEntryEpochSec.value,
                caloriesInputValue = _caloriesInput.value
            )
        }

        if (
            _perPieceGram.value.isEmpty() &&
            _perHundredGram.value.isEmpty() &&
            _caloriesInput.value.isEmpty()
        ) {
            actionChangeAlertDialogValue(true)
        }
    }

    fun categoryHandling(category: String) {
        viewModelScope.launch(Dispatchers.IO) {

            // saving option 1: The category is not in the database yet and there is NO barcode displayed to the user: Save the category only
            if (!_categories.value.contains(category) && !_noBarcodeYetInfoTitle.value) {
                database.appDao().insertCategory(Category(category = category))
            }

            // saving option 2: The category is not in the database yet and a barcode is displayed to the user: Save the category and the barcode in a new row
            if (!_categories.value.contains(category) && _noBarcodeYetInfoTitle.value) {
                database.appDao().insertCategory(
                    Category(
                        category = category,
                        barcodeNumber = _barcodeNumber.value
                    )
                )
                removeLastBarcodeInput()
            }

            //saving option 3: The category is already in the database and the user is displayed a barcode: Get that category from the database and save the barcode with it
            if (_categories.value.contains(category) && _noBarcodeYetInfoTitle.value) {
                val categoryRow = database.appDao().getCategoryByCategoryName(category)
                categoryRow.barcodeNumber = _barcodeNumber.value
                database.appDao().updateCategory(categoryRow)
                removeLastBarcodeInput()
            }
        }
    }


    //Check Calories Threshold: START
    fun checkThresholdForCaloriesInput() {
        viewModelScope.launch(Dispatchers.IO) {
            val dateString = HelperMethods.convertTimestampToDateString(
                dateOfEntryEpochSec.value,
                "yyyy-MM-dd"
            )
            val databaseSumCalories =
                database.appDao().checkIfCaloriesThresholdIsBreached(dateString) ?: 0
            Log.d("tag", "DatabaseSumCalories: " + databaseSumCalories)


            withContext(Dispatchers.Main) {
                if (databaseSumCalories > sharedPrefsMain.getInt("caloriesThresholdValue", 0)) {
                    _alertCaloriesThreshold.value = true
                }
            }
        }
    }
    //Check Calories Threshold: END


    //Loading an Entry: Start
    fun loadLastEntryForGivenCategory(keyboardController: SoftwareKeyboardController?) {
        keyboardController?.hide()
        getEntryByCategory(_categorySelected.value)
    }

    private fun getEntryByCategory(category: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val entryReply =
                database.appDao().checkIfGramValueExistsForCategory(category)

            withContext(Dispatchers.Main) {

                when {
                    entryReply == null -> {
                        _perPieceGram.value = ""
                        _perHundredGram.value = ""
                        _isHundredTabIndex.value = 0
                        actionNoDataForChosenCategorySnackbarShownChange(true)

                    }

                    entryReply.perPieceGram != 0 -> {
                        _perPieceGram.value = entryReply.perPieceGram.toString()
                        _perHundredGram.value = ""
                        _isHundredTabIndex.value = 1
                    }

                    entryReply.perHundredGram != 0 -> {
                        _perHundredGram.value = entryReply.perHundredGram.toString()
                        _perPieceGram.value = ""
                        _isHundredTabIndex.value = 0
                    }

                    else -> Log.e(
                        "CounterVM",
                        "Loading entry from database by its category did not succeed"
                    )
                }

            }

        }
    }
    //Loading an Entry: End

    //Barcode: Start
    fun scanBarcode() {
        barcodeScanner.startScan()
            .addOnSuccessListener { barcode ->
                println("Barcode: " + barcode.rawValue)
                _barcodeNumber.value = barcode.rawValue.toString()

                if (_barcodeNumber.value.isEmpty()) {
                    println("Barcode Scanning did not work")
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        val categoryFromBarcode =
                            database.appDao().getCategoryByBarcodeNumber(_barcodeNumber.value)
                        if (categoryFromBarcode.isNullOrEmpty()) {
                            _noBarcodeYetInfoTitle.value = true
                        } else {
                            _categorySelected.value = categoryFromBarcode
                            getEntryByCategory(categoryFromBarcode)
                        }
                    }
                }
            }
            .addOnFailureListener { e -> println("Barcode scanning failure") }
    }

    fun removeLastBarcodeInput() {
        _noBarcodeYetInfoTitle.value = false
    }
    //Barcode: End


    //Actions Start: User actions reported by the UI to the ViewModel

    fun actionChangeDatePickerVisibility(datePickerShownValue: Boolean) {
        _datePickerShown.value = datePickerShownValue
    }

    fun actionChangeNOBarcodeInfoYetDescription(visibility: Boolean) {
        _noBarcodeYetInfoDescription.value = visibility
    }

    fun actionNoDataForChosenCategorySnackbarShownChange(isShown: Boolean) {
        _noDataForChosenCategorySnackbarShown.value = isShown
    }

    fun actionShowDeleteAlertDialog(item: IEntry) {
        when (item) {
            is Entry -> {
                _itemToDeleteIsEntrySugar.value = true
                _itemToDeleteEntrySugar.value = item
                _categoryItemDeleteObject.value = item
            }

            is EntryCalories -> {
                _itemToDeleteIsEntrySugar.value = false
                _itemToDeleteEntryCalories.value = item
            }

            else -> Log.d("DeleteAlertDialog", "Delete action did not work")
        }
        _showDeleteDialog.value = true
    }

    fun actionDismissDeleteAlertDialog() {
        _showDeleteDialog.value = false
    }

    fun actionDeleteSpecificEntryRow(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_itemToDeleteIsEntrySugar.value) {
                database.appDao().deleteSpecificEntryRow(id)
            } else {
                database.appDao().deleteSpecificEntryCaloriesRow(id)
            }
        }
    }

    fun actionChangeSelectedCategory(categorySelected: String) {
        _categorySelected.value = categorySelected
    }

    fun actionChangeCategoryFieldExpanded(isExpanded: Boolean) {
        _categoryFieldExpanded.value = isExpanded
    }

    fun actionChangeCategoryFieldSize(newSize: Size) {
        _categoryFieldSize.value = newSize
    }

    fun actionSetIsHundredTabIndex(tabIndex: Int) {
        _isHundredTabIndex.value = tabIndex
    }

    fun actionChangeGramCountMode(gramCountMode: GramCountMode) {
        _gramCountMode.value = gramCountMode
    }

    fun actionPerPieceGramChange(perPieceGram: String) {
        _perPieceGram.value = perPieceGram
    }

    fun actionPerPieceAmountChange(perPieceAmount: String) {
        _perPieceAmount.value = perPieceAmount
    }

    fun actionPerHundredChange(perHundredGram: String) {
        _perHundredGram.value = perHundredGram
    }

    fun actionPerHundredQuantityChange(perHundredQuantity: String) {
        _perHundredQuantity.value = perHundredQuantity
    }

    fun actionChangeAlertDialogValue(showDialog: Boolean) {
        _alertDialog.value = showDialog
    }

    fun actionDismissAlertDialog() {
        _alertDialog.value = false
    }

    fun actionGramThresholdKeepLastEntry() {
        _alertDialogGramThreshold.value = false
    }

    fun actionGramThresholdDeleteLastEntry() {
        _alertDialogGramThreshold.value = false
        viewModelScope.launch(Dispatchers.IO) {
            database.appDao().deleteLastEntry()
        }
    }

    fun actionCaloriesThresholdKeepLastEntry() {
        _alertCaloriesThreshold.value = false
    }

    fun actionCaloriesThresholdDeleteLastEntry() {
        _alertCaloriesThreshold.value = false
        viewModelScope.launch(Dispatchers.IO) {
            database.appDao().deleteLastEntryCalories()
        }
    }

    fun actionChangeDateOfEntryM3(epochSec: Long) {
        _dateOfEntryEpochSec.value = epochSec
    }

    fun actionCaloriesChange(caloriesInKcal: String) {
        _caloriesInput.value = caloriesInKcal
    }

    fun actionChangeSegmentedButtonIndex(index: Int) {
        _segmentedButtonIndex.value = index
        println("Index: $index")
    }

    //Actions End

}
