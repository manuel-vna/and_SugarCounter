package com.example.mva_sugarcounter.viewModels


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.mva_sugarcounter.data.Category
import com.example.mva_sugarcounter.data.Entry
import com.example.mva_sugarcounter.data.GramCountMode
import com.example.mva_sugarcounter.database.AppDatabase
import com.example.mva_sugarcounter.util.HelperMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.roundToInt


class CounterVM(application: Application) : AndroidViewModel(application) {

    val helperMethods: HelperMethods = HelperMethods(application)
    private val database = AppDatabase.getInstance(this.getApplication())

    val epochTimestampSecondsNow = System.currentTimeMillis() / 1000

    //StateFlow: START

    val _datePickerShown = MutableStateFlow(false)
    val datePickerShown = _datePickerShown.asStateFlow()

    val _dateOfEntryEpochSec = MutableStateFlow(epochTimestampSecondsNow)
    val dateOfEntryEpochSec = _dateOfEntryEpochSec.asStateFlow()

    val _categorySelected = MutableStateFlow("")
    val categorySelected = _categorySelected.asStateFlow()

    val _categories = MutableStateFlow(listOf<String>())
    val categories = _categories.asStateFlow()

    val _isHundredTabIndex = MutableStateFlow(0)
    val isHundredTabIndex = _isHundredTabIndex.asStateFlow()

    var _gramCountMode = MutableStateFlow(GramCountMode.PerHundred)
    var gramCountMode = _gramCountMode.asStateFlow()

    var _perPieceGram = MutableStateFlow("")
    val perPieceGram = _perPieceGram.asStateFlow()

    var _perPieceAmount = MutableStateFlow("")
    val perPieceAmount = _perPieceAmount.asStateFlow()

    var _perHundredGram = MutableStateFlow("")
    val perHundredGram = _perHundredGram.asStateFlow()

    var _perHundredQuantity = MutableStateFlow("")
    val perHundredQuantity = _perHundredQuantity.asStateFlow()

    val _alertDialog = MutableStateFlow(false)
    val alertDialog = _alertDialog.asStateFlow()

    val _categoryItemDeleteDialog = MutableStateFlow(false)
    val categoryItemDeleteDialog = _categoryItemDeleteDialog.asStateFlow()

    val _categoryItemDeleteObject = MutableStateFlow(
        Entry(
            0, 0, "", "", true,
            0, 0, 0, 0, 0
        )
    )
    val categoryItemDeleteObject = _categoryItemDeleteObject.asStateFlow()

    val _alertDialogGramThreshold = MutableStateFlow(false)
    val alertDialogGramThreshold = _alertDialogGramThreshold.asStateFlow()

    val _barcodeNoEntry = MutableStateFlow(false)
    val barcodeNoEntry = _barcodeNoEntry.asStateFlow()
    //StateFlow: END

    // Timestamps: BEGIN
    private val today = LocalDate.now()
    private val startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
    private val endOfToday =
        today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() - 1
    // Timestamps: END

    // StateFlow that is observed by UI
    private val _savedEntriesToday =
        MutableStateFlow(emptyMap<Pair<String, String>, List<Entry>>())
    val savedEntriesToday = _savedEntriesToday.asStateFlow()

    // Observer that is used to observe Dao of RoomDB
    private val categoryObserver = Observer<List<Category>> {
        _categories.value = it.map { it.category }
    }

    // Observer that is used to observe Dao of RoomDB
    private val todayObserverObject = Observer<List<Entry>> {
        val savedSugarCountGrouped = helperMethods.groupCounterItemsInGroupsByDay(it)
        _savedEntriesToday.value = savedSugarCountGrouped
    }

    // When this ViewModal is initialized, tell the above created observer what has to be observed and how long
    init {
        database.appDao().getAllCategories().observeForever(categoryObserver)

        database.appDao().getEntries(startOfToday, endOfToday)
            .observeForever(todayObserverObject)
    }

    override fun onCleared() {
        super.onCleared()
        // Stop observing at Dao of RoomDB when this ViewModel is cleared
        database.appDao().getAllCategories().removeObserver(categoryObserver)

        database.appDao().getEntries(startOfToday, endOfToday)
            .removeObserver(todayObserverObject)
    }

    //Saving an Entry: Start
    fun saveEntry(category: String) {

        var perPieceGramInt = 0
        var perPieceAmountInt = 1
        var perHundredGramDouble = 0.0
        var perHundredQuantityDouble = 0.0

        if (gramCountMode.value == GramCountMode.PerHundred) {
            if (perHundredGram.value.isNotEmpty()) perHundredGramDouble =
                perHundredGram.value.toDouble()
            if (perHundredQuantity.value.isNotEmpty()) perHundredQuantityDouble =
                perHundredQuantity.value.toDouble()

            if (perHundredGram.value.isEmpty()) {
                _alertDialog.value = true
            } else {
                saveEntryInDatabase(
                    category = category,
                    isPerHundred = true,
                    perHundredGramInt = perHundredGramDouble.toInt(),
                    perHundredQuantityInt = perHundredQuantityDouble.toInt(),
                    perPieceGramInt = 0,
                    perPieceAmountInt = 0,
                    gramTotalInt = ((perHundredGramDouble / 100) * perHundredQuantityDouble).roundToInt()  // rule of three: Calculate sugar on basis of the quantity eaten
                )
            }
        } else {
            if (perPieceGram.value.isNotEmpty()) perPieceGramInt = perPieceGram.value.toInt()
            if (perPieceAmount.value.isNotEmpty()) perPieceAmountInt = perPieceAmount.value.toInt()

            if (perPieceGram.value.isEmpty()) {
                _alertDialog.value = true
            } else {
                saveEntryInDatabase(
                    category = category,
                    isPerHundred = false,
                    perHundredGramInt = 0,
                    perHundredQuantityInt = 0,
                    perPieceGramInt = perPieceGramInt,
                    perPieceAmountInt = perPieceAmountInt,
                    gramTotalInt = perPieceGramInt * perPieceAmountInt // multiplying gram per piece value with amount of itmes eaten
                )
            }
        }

    }

    private fun saveEntryInDatabase(
        category: String,
        isPerHundred: Boolean,
        perHundredGramInt: Int,
        perHundredQuantityInt: Int,
        perPieceGramInt: Int,
        perPieceAmountInt: Int,
        gramTotalInt: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            database.appDao().insertEntry(
                Entry(
                    currentTimestamp = dateOfEntryEpochSec.value,
                    date = helperMethods.formatDateToString(
                        dateOfEntryEpochSec.value,
                        "YYYY-MM-dd"
                    ),
                    category = category,
                    isPerHundred = isPerHundred,
                    perPieceGram = perPieceGramInt,
                    perPieceAmount = perPieceAmountInt,
                    perHundredGram = perHundredGramInt,
                    perHundredQuantity = perHundredQuantityInt,
                    gramTotal = gramTotalInt
                )
            )
            // check if database already contains the given category string
            if (!_categories.value.contains(category)) {
                database.appDao().insertCategory(Category(category = category))
            }

            checkGramThreshold()
        }
    }
    //Saving an Entry: End


    //Checking an Entry: Start
    private fun checkGramThreshold() {
        viewModelScope.launch(Dispatchers.IO) {
            val dateString = helperMethods.formatDateToString(
                dateOfEntryEpochSec.value,
                "YYYY-MM-dd"
            )
            val databaseSum = database.appDao().checkIfGramThresholdIsBreached(dateString)

            withContext(Dispatchers.Main) {
                databaseSum?.let {
                    if (databaseSum > 45) {
                        _alertDialogGramThreshold.value = true
                    }
                }
            }
        }
    }
    //Checking an Entry: End


    //Loading an Entry: End
    fun loadLastEntryForGivenCategory() {
        viewModelScope.launch(Dispatchers.IO) {

            val entryReply =
                database.appDao().checkIfGramValueExistsForCategory(_categorySelected.value)

            withContext(Dispatchers.Main) {
                if (entryReply?.perPieceGram != 0) {
                    _perPieceGram.value = entryReply?.perPieceGram.toString()
                    _perHundredGram.value = ""
                    _isHundredTabIndex.value = 1
                } else {
                    _perHundredGram.value = entryReply.perHundredGram.toString()
                    _perPieceGram.value = ""
                    _isHundredTabIndex.value = 0
                }
            }
        }
    }
    //Loading an Entry: End

    fun calculateTotalGramPerDayBlock(valueList: List<Entry>): Int {
        return helperMethods.calculateTotalGramPerDayBlock(valueList)
    }
    
    //Actions Start: User actions reported by the UI to the ViewModel

    fun actionChangeDatePickerVisibility(datePickerShownValue: Boolean) {
        _datePickerShown.value = datePickerShownValue
    }

    fun actionShowDeleteAlertDialog(item: Entry) {
        _categoryItemDeleteObject.value = item
        _categoryItemDeleteDialog.value = true
    }

    fun actionDismissDeleteAlertDialog() {
        _categoryItemDeleteDialog.value = false
    }

    fun actionDeleteSpecificEntryRow(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            database.appDao().deleteSpecificEntryRow(id)
        }
    }

    fun actionChangeSelectedCategory(categorySelected: String) {
        _categorySelected.value = categorySelected
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

    fun actionChangeDateOfEntryM3(epochSec: Long) {
        _dateOfEntryEpochSec.value = epochSec
    }

    fun actionShowBarcodeNoEntryDialog() {
        _barcodeNoEntry.value = true
    }

    fun actionDismissBarcodeNoEntryDialog() {
        _barcodeNoEntry.value = false
    }

    //Actions End

}
