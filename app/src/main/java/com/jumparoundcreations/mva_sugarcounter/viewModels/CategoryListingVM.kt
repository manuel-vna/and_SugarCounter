package com.jumparoundcreations.mva_sugarcounter.viewModels

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.mva_sugarcounter.data.Category
import com.jumparoundcreations.mva_sugarcounter.data.states.CategoryListingStates
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CategoryListingVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()

    //SateFlows: START
    private val _categories = MutableStateFlow(emptyMap<Char, List<Category>>())
    val categories = _categories.asStateFlow()

    val _categoryDeleteAlertDialog = MutableStateFlow(false)
    val categoryDeleteAlertDialog = _categoryDeleteAlertDialog.asStateFlow()

    val _categoryDeleteId = MutableStateFlow(0)
    val categoryDeleteId = _categoryDeleteId.asStateFlow()

    val _deletionCheckboxes = MutableStateFlow(CategoryListingStates())
    val deletionCheckboxes = _deletionCheckboxes.asStateFlow()

    //SateFlows: END


    //Observer _categories: START
    // Observer that is used to observe Dao of RoomDB
    private val categoriesObserver = Observer<List<Category>> { it ->
        val sortedCategories =
            it.filter { it.category.isNotBlank() }.groupBy(keySelector = { it.category.first() })
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
    fun actionShowDeletionCheckboxes() {
        _deletionCheckboxes.value = CategoryListingStates(
            deletionCheckboxesDisplayed = true,
            deletionButtonsDisplayed = true,
        )
    }

    fun actionHideDeletionCheckboxes() {
        _deletionCheckboxes.value = CategoryListingStates(
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
                        category = category.category,
                        deletionCheckbox = false
                    )
                )
            } else {
                database.appDao().updateCategory(
                    Category(
                        id = category.id,
                        category = category.category,
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
    //Actions: END


}