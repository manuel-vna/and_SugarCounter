package com.example.mva_sugarcounter.viewModels


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.mva_sugarcounter.data.Category
import com.example.mva_sugarcounter.data.Entry
import com.example.mva_sugarcounter.database.AppDatabase
import com.example.mva_sugarcounter.util.HelperMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId


class CounterVM(application: Application) : AndroidViewModel(application) {

    val helperMethods: HelperMethods = HelperMethods(application)
    private val database = AppDatabase.getInstance(this.getApplication())

    val epochTimestampSecondsNow = System.currentTimeMillis() / 1000

    //StateFlow: START
    val _dateOfEntryEpochSec = MutableStateFlow(epochTimestampSecondsNow)
    val dateOfEntryEpochSec = _dateOfEntryEpochSec.asStateFlow()

    val _categories = MutableStateFlow(listOf<String>())
    val categories = _categories.asStateFlow()

    val _date = MutableStateFlow("")
    val date = _date.asStateFlow()

    var _gramValue = MutableStateFlow("")
    val gramValue = _gramValue.asStateFlow()

    var _amountValue = MutableStateFlow("")
    val amountValue = _amountValue.asStateFlow()

    val _alertDialog = MutableStateFlow(false)
    val alertDialog = _alertDialog.asStateFlow()

    val _categoryItemDeleteDialog = MutableStateFlow(false)
    val categoryItemDeleteDialog = _categoryItemDeleteDialog.asStateFlow()

    val _categoryItemDeleteObject = MutableStateFlow(Entry(0, 0, "", 0, 0, "", 0))
    val categoryItemDeleteObject = _categoryItemDeleteObject.asStateFlow()

    val _alertDialogGramThreshold = MutableStateFlow(false)
    val alertDialogGramThreshold = _alertDialogGramThreshold.asStateFlow()
    //StateFlow: END

    // Timestamps: BEGIN
    private val today = LocalDate.now()
    private val startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() //* 1000
    private val endOfToday =
        today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() - 1 //* 1000 - 1
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
        var gramValueInt = 0
        var amountValueInt = 1

        if (gramValue.value.isNotEmpty()) gramValueInt = gramValue.value.toInt()
        if (amountValue.value.isNotEmpty()) amountValueInt = amountValue.value.toInt()

        if (gramValue.value.isEmpty()) {
            getLastEntryOfCategory(category, amountValueInt)
        } else {
            saveEntryInDatabase(category, gramValueInt, amountValueInt)
        }
    }

    private fun getLastEntryOfCategory(category: String, amountValueInt: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val asyncAnswer = async { checkForExistingGramValue(category) }
            val sugarCounterRow = asyncAnswer.await()

            // if an entry of this category exists already, take the gram value from it and save it
            if (sugarCounterRow != null) {
                saveEntryInDatabase(category, sugarCounterRow.gramItem, amountValueInt)
            }
            // if there is no entry for that category yet, prompt the user that the field gram has to be filled
            else {
                withContext(Dispatchers.Main) {
                    Log.d("Tag", "Error: Data has to be entered for gram")
                    _alertDialog.value = true
                }
            }
        }
    }

    private fun saveEntryInDatabase(category: String, gramValueInt: Int, amountValueInt: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            database.appDao().insertEntry(
                Entry(
                    currentTimestamp = dateOfEntryEpochSec.value, //System.currentTimeMillis(),
                    date = helperMethods.formatDateToString(
                        dateOfEntryEpochSec.value,
                        "YYYY-MM-dd"
                    ),
                    gramItem = gramValueInt,
                    amount = amountValueInt,
                    category = category,
                    gramTotal = gramValueInt * amountValueInt
                )
            )
            // check if database already contains the given category string
            if (!_categories.value.contains(category)) {
                database.appDao().insertCategory(Category(category = category))
            }

            checkGramThreshold()
        }
    }

    private fun checkGramThreshold() {
        viewModelScope.launch(Dispatchers.IO) {
            val dateString = helperMethods.formatDateToString(
                epochTimestampSecondsNow,
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

    fun loadLastEntryForGivenCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val asyncAnswer = async { checkForExistingGramValue(category) }
            val sugarCounterRow = asyncAnswer.await()

            withContext(Dispatchers.Main) {
                Log.d("Tag", "Set field gram with last entry of chosen category")
                _gramValue.value = sugarCounterRow?.gramItem.toString()
                _amountValue.value = ""
            }
        }
    }

    private fun checkForExistingGramValue(category: String): Entry? {
        return database.appDao().checkIfGramValueExistsForCategory(category)
    }
    //Saving an Entry: End


    fun calculateTotalGramPerDayBlock(valueList: List<Entry>): Int {
        return helperMethods.calculateTotalGramPerDayBlock(valueList)
    }


    //Actions Start: User actions reported by the UI to the ViewModel
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

    fun actionGramChange(gramValue: String) {
        _gramValue.value = gramValue
    }

    fun actionAmountChange(amountValue: String) {
        _amountValue.value = amountValue
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

    fun actionChangeDateOfEntry(localDate: LocalDate) {
        val zoneId = ZoneId.systemDefault()
        val epoch: Long = localDate.atStartOfDay(zoneId).toEpochSecond()
        _dateOfEntryEpochSec.value = epoch
    }

    //Actions End

}
