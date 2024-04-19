package com.example.mva_sugarcounter.viewModels


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.mva_sugarcounter.database.AppDatabase
import com.example.mva_sugarcounter.data.Category
import com.example.mva_sugarcounter.data.Entry
import com.example.mva_sugarcounter.util.HelperMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CounterVM(application: Application) : AndroidViewModel(application) {

    val helperMethods: HelperMethods = HelperMethods(application)
    private val database = AppDatabase.getInstance(this.getApplication())

    //StateFlow: START
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

    //StateFlow: END


    //get timestamp of yesterday
    val currentTimestamp = System.currentTimeMillis()
    val nowMinus1Day =
        currentTimestamp - 90000000 // 86400 seconds (1 day) + 3600 seconds (1 hour) + 000 ( to milliseconds)
    val nowMinus30days = currentTimestamp - 2592000000
    val nowMinus150days = currentTimestamp - 12960000000

    // StateFlow that is observed by UI
    private val _savedHistory =
        MutableStateFlow(emptyMap<Pair<String, String>, List<Entry>>())
    val savedHistory = _savedHistory.asStateFlow()

    private val _savedEntriesNowMinus1Day =
        MutableStateFlow(emptyMap<Pair<String, String>, List<Entry>>())
    val savedEntriesNowMinus1Day = _savedEntriesNowMinus1Day.asStateFlow()

    // Observer that is used to observe Dao of RoomDB
    private val historyObserver = Observer<List<Entry>> {
        val savedSugarCountGrouped = helperMethods.groupCounterItemsInGroupsByDay(it)
        _savedHistory.value = savedSugarCountGrouped
    }
    private val categoryObserver = Observer<List<Category>> {
        _categories.value = it.map { it.category }
    }

    // Observer that is used to observe Dao of RoomDB
    private val nowMinus1DayObserverObject = Observer<List<Entry>> {
        val savedSugarCountGrouped = helperMethods.groupCounterItemsInGroupsByDay(it)
        _savedEntriesNowMinus1Day.value = savedSugarCountGrouped
    }


    // When this ViewModal is initialized, tell the above created observer what has to be observed and how long
    init {
        database.appDao().getEntries(nowMinus1Day, nowMinus30days)
            .observeForever(historyObserver)

        database.appDao().getAllCategories().observeForever(categoryObserver)

        database.appDao().getEntries(9999999999999, nowMinus1Day)
            .observeForever(nowMinus1DayObserverObject)

    }

    override fun onCleared() {
        super.onCleared()
        // Stop observing at Dao of RoomDB when this ViewModel is cleared
        database.appDao().getEntries(nowMinus1Day, nowMinus30days)
            .removeObserver(historyObserver)

        database.appDao().getAllCategories().removeObserver(categoryObserver)

        database.appDao().getEntries(9999999999999, nowMinus1Day)
            .removeObserver(nowMinus1DayObserverObject)

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

            // if an entry of this catefory exists already, take the gram value from it and save it
            if (sugarCounterRow != null) {
                saveEntryInDatabase(category, sugarCounterRow.gramItem, amountValueInt)
            }
            // if there is noe entry for that category yet, prompt the user that the field gram has to be filled
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
                    currentTimestamp = System.currentTimeMillis(),
                    date = helperMethods.formatDateToString(
                        System.currentTimeMillis(),
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
    //Actions End


    // Temporary unused: Start
    fun deleteEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            database.appDao().deleteEntriesOlderThanOneWeek(nowMinus30days)
        }
    }
    // Temporary unused: End

}
