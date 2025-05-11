package com.jumparoundcreations.mva_sugarcounter.viewModels

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.Category
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.CategoryStates
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CategoryVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()

    val letters = mapOf(
        Pair("A", 0),
        Pair("B", 1),
        Pair("C", 2),
        Pair("D", 3),
        Pair("E", 4),
        Pair("F", 5),
        Pair("G", 6),
        Pair("H", 7),
        Pair("I", 8),
        Pair("J", 9),
        Pair("K", 10),
        Pair("L", 11),
        Pair("M", 12),
        Pair("N", 13),
        Pair("O", 14),
        Pair("P", 15),
        Pair("Q", 16),
        Pair("R", 17),
        Pair("S", 18),
        Pair("T", 19),
        Pair("U", 20),
        Pair("V", 21),
        Pair("W", 22),
        Pair("X", 23),
        Pair("Y", 24),
        Pair("Z", 25),
    )

    //SateFlows: START
    private val _categories = MutableStateFlow(emptyMap<Char, List<Category>>())
    val categories = _categories.asStateFlow()

    private val _categoryListScrollState = MutableStateFlow(LazyListState(0, 0))
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
        Entry(
            id = 0,
            currentTimestamp = 1L,
            date = "",
            category = "",
            isPerHundred = false,
            perHundredGram = 0,
            perHundredQuantity = 0,
            perPieceGram = 0,
            perPieceAmount = 0,
            gramTotal = 0
        )
    )
    val entrySugarForClickedCategory = _entrySugarForClickedCategory.asStateFlow()

    private val _entryCaloriesForClickedCategory = MutableStateFlow(
        EntryCalories(
            id = 0,
            currentTimestamp = 1L,
            date = "",
            category = "",
            caloriesPerPiece = 0,
            caloriesAmount = 1,
            caloriesTotal = 0
        )
    )
    val entryCaloriesForClickedCategory = _entryCaloriesForClickedCategory.asStateFlow()

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

            val lastEntrySugar: Entry? =
                database.appDao().checkIfGramValueExistsForCategory(clickedCategory.category)
            _entrySugarForClickedCategory.value = lastEntrySugar ?: Entry(
                id = 0,
                currentTimestamp = 0L,
                date = "",
                category = "",
                isPerHundred = false,
                perHundredGram = 0,
                perHundredQuantity = 0,
                perPieceGram = 0,
                perPieceAmount = 0,
                gramTotal = 0
            )

            val lastEntryCalories: EntryCalories? =
                database.appDao().checkIfCaloriesValueExistsForCategory(clickedCategory.category)
            _entryCaloriesForClickedCategory.value = lastEntryCalories ?: EntryCalories(
                id = 0,
                currentTimestamp = 0L,
                date = "",
                category = "",
                caloriesPerPiece = 0,
                caloriesAmount = 1,
                caloriesTotal = 0
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
                        barcodeNumber = "123456789"
                    )
                )
            }
        }
    }
    //Testing Purposes: END


}