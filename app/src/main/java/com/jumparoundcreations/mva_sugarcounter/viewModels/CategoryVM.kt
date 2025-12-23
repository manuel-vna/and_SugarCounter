package com.jumparoundcreations.mva_sugarcounter.viewModels

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.Category
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.CategoryStates
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.util.DatabaseConstants
import com.jumparoundcreations.mva_sugarcounter.util.TestData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CategoryVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()

    companion object {
        private const val LETTER_A = "A"
        private const val LETTER_B = "B"
        private const val LETTER_C = "C"
        private const val LETTER_D = "D"
        private const val LETTER_E = "E"
        private const val LETTER_F = "F"
        private const val LETTER_G = "G"
        private const val LETTER_H = "H"
        private const val LETTER_I = "I"
        private const val LETTER_J = "J"
        private const val LETTER_K = "K"
        private const val LETTER_L = "L"
        private const val LETTER_M = "M"
        private const val LETTER_N = "N"
        private const val LETTER_O = "O"
        private const val LETTER_P = "P"
        private const val LETTER_Q = "Q"
        private const val LETTER_R = "R"
        private const val LETTER_S = "S"
        private const val LETTER_T = "T"
        private const val LETTER_U = "U"
        private const val LETTER_V = "V"
        private const val LETTER_W = "W"
        private const val LETTER_X = "X"
        private const val LETTER_Y = "Y"
        private const val LETTER_Z = "Z"

        private const val INDEX0 = 0
        private const val INDEX1 = 1
        private const val INDEX2 = 2
        private const val INDEX3 = 3
        private const val INDEX4 = 4
        private const val INDEX5 = 5
        private const val INDEX6 = 6
        private const val INDEX7 = 7
        private const val INDEX8 = 8
        private const val INDEX9 = 9
        private const val INDEX10 = 10
        private const val INDEX11 = 11
        private const val INDEX12 = 12
        private const val INDEX13 = 13
        private const val INDEX14 = 14
        private const val INDEX15 = 15
        private const val INDEX16 = 16
        private const val INDEX17 = 17
        private const val INDEX18 = 18
        private const val INDEX19 = 19
        private const val INDEX20 = 20
        private const val INDEX21 = 21
        private const val INDEX22 = 22
        private const val INDEX23 = 23
        private const val INDEX24 = 24
        private const val INDEX25 = 25

        private const val SCROLL_STATE_START_INDEX = 0


    }

    val letters = mapOf(
        Pair(LETTER_A, INDEX0),
        Pair(LETTER_B, INDEX1),
        Pair(LETTER_C, INDEX2),
        Pair(LETTER_D, INDEX3),
        Pair(LETTER_E, INDEX4),
        Pair(LETTER_F, INDEX5),
        Pair(LETTER_G, INDEX6),
        Pair(LETTER_H, INDEX7),
        Pair(LETTER_I, INDEX8),
        Pair(LETTER_J, INDEX9),
        Pair(LETTER_K, INDEX10),
        Pair(LETTER_L, INDEX11),
        Pair(LETTER_M, INDEX12),
        Pair(LETTER_N, INDEX13),
        Pair(LETTER_O, INDEX14),
        Pair(LETTER_P, INDEX15),
        Pair(LETTER_Q, INDEX16),
        Pair(LETTER_R, INDEX17),
        Pair(LETTER_S, INDEX18),
        Pair(LETTER_T, INDEX19),
        Pair(LETTER_U, INDEX20),
        Pair(LETTER_V, INDEX21),
        Pair(LETTER_W, INDEX22),
        Pair(LETTER_X, INDEX23),
        Pair(LETTER_Y, INDEX24),
        Pair(LETTER_Z, INDEX25),
    )

    //SateFlows: START
    private val _categories = MutableStateFlow(emptyMap<Char, List<Category>>())
    val categories = _categories.asStateFlow()

    private val _categoryListScrollState =
        MutableStateFlow(LazyListState(SCROLL_STATE_START_INDEX, SCROLL_STATE_START_INDEX))
    val categoryListScrollState = _categoryListScrollState.asStateFlow()

    private val _scrollSearchMenuExpanded = MutableStateFlow(false)
    val scrollSearchMenuExpanded = _scrollSearchMenuExpanded.asStateFlow()

    private val _deletionCheckboxes = MutableStateFlow(CategoryStates())
    val deletionCheckboxes = _deletionCheckboxes.asStateFlow()

    private val _categoryBottomSheetShown = MutableStateFlow(false)
    val categoryBottomSheetShown = _categoryBottomSheetShown.asStateFlow()

    private val _clickedCategory =
        MutableStateFlow(Category(category = "", deletionCheckbox = false, barcodeNumber = ""))
    val clickedCategory = _clickedCategory.asStateFlow()

    private val _entrySugarForClickedCategory = MutableStateFlow(
        SugarEntry(
            id = DatabaseConstants.DEFAULT_DATABASE_INT,
            currentTimestamp = DatabaseConstants.DEFAULT_DATABASE_TIMESTAMP,
            date = DatabaseConstants.DEFAULT_DATABASE_STRING,
            category = DatabaseConstants.DEFAULT_DATABASE_STRING,
            entryType = DatabaseConstants.DEFAULT_GRAM_COUNT_MODE,
            gram = DatabaseConstants.DEFAULT_DATABASE_DOUBLE,
            quantity = DatabaseConstants.DEFAULT_DATABASE_DOUBLE,
            gramTotal = DatabaseConstants.DEFAULT_DATABASE_DOUBLE
        )
    )
    val entrySugarForClickedCategory = _entrySugarForClickedCategory.asStateFlow()

    //SateFlows: END


    //Observer _categories: START
    // Observer that is used to observe Dao of RoomDB
    private val categoriesObserver = Observer<List<Category>> { it ->
        val sortedCategories =
            it.filter { it.category.isNotBlank() }
                .groupBy(keySelector = { it.category.uppercase().first() })
                .toSortedMap()
        _categories.value = sortedCategories
    }

    init {
        database.appDao().getAllCategories()
            .observeForever(categoriesObserver)
    }

    override fun onCleared() {
        super.onCleared()
        // Stop observing at Dao of RoomDB when this ViewModel is cleared
        database.appDao().getAllCategories()
            .removeObserver(categoriesObserver)
    }
    //Observer _categories: END


    //Actions: START
    fun actionHandleCategoryScrollState(index: Int, scrollState: LazyListState) {
        viewModelScope.launch {
            scrollState.scrollToItem(index)
        }
        _categoryListScrollState.value = scrollState
    }

    fun actionChangeScrollSearchMenuExpanded(expanded: Boolean) {
        _scrollSearchMenuExpanded.value = expanded
    }

    fun actionShowDeletionCheckboxes() {
        _deletionCheckboxes.value = CategoryStates(
            deletionCheckboxesDisplayed = true,
            deletionButtonsDisplayed = true,
        )
    }

    fun actionHideDeletionCheckboxes() {
        _deletionCheckboxes.value = CategoryStates(
            deletionCheckboxesDisplayed = false,
            deletionButtonsDisplayed = false,
        )
    }

    fun actionChangeDeleteCheckbox(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            if (category.deletionCheckbox) {
                database.appDao().updateCategory(
                    Category(
                        id = category.id,
                        category = category.category.trim(),
                        deletionCheckbox = false
                    )
                )
            } else {
                database.appDao().updateCategory(
                    Category(
                        id = category.id,
                        category = category.category.trim(),
                        deletionCheckbox = true
                    )
                )
            }
        }
    }

    fun actionDeleteCheckedCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            database.appDao().deleteCheckedCategories()
        }
    }

    fun actionChangeCategoryBottomSheetShown(
        categoryBottomSheet: Boolean,
        clickedCategory: Category?
    ) {
        _categoryBottomSheetShown.value = categoryBottomSheet
        clickedCategory?.let {
            _clickedCategory.value = it
            retrieveLastEntriesForClickedCategory(clickedCategory = it)
        }
    }
    //Actions: END

    private fun retrieveLastEntriesForClickedCategory(clickedCategory: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastEntrySugar: SugarEntry? =
                database.appDao().checkIfEntryExistsForCategory(clickedCategory.category)
            _entrySugarForClickedCategory.value = lastEntrySugar ?: SugarEntry(
                id = DatabaseConstants.DEFAULT_DATABASE_INT,
                currentTimestamp = DatabaseConstants.DEFAULT_DATABASE_TIMESTAMP,
                date = DatabaseConstants.DEFAULT_DATABASE_STRING,
                category = DatabaseConstants.DEFAULT_DATABASE_STRING,
                entryType = DatabaseConstants.DEFAULT_GRAM_COUNT_MODE,
                gram = DatabaseConstants.DEFAULT_DATABASE_DOUBLE,
                quantity = DatabaseConstants.DEFAULT_DATABASE_DOUBLE,
                gramTotal = DatabaseConstants.DEFAULT_DATABASE_DOUBLE
            )
        }
    }

    //Testing Purposes: START
    //Call to this method has to be added somewhere
    fun addCategoryTestData() {
        viewModelScope.launch(Dispatchers.IO) {
            repeat(150) {
                val lettersKeys = letters.keys
                val randomLetter1 = lettersKeys.random()
                val randomLetter2 = lettersKeys.random()
                val randomLetter3 = lettersKeys.random()
                val randomWord = randomLetter1 + randomLetter2 + randomLetter3

                database.appDao().insertCategory(
                    Category(
                        category = randomWord.trim(),
                        barcodeNumber = TestData.TEST_BARCODE_NUMBER
                    )
                )
            }
        }
    }
    //Testing Purposes: END


}