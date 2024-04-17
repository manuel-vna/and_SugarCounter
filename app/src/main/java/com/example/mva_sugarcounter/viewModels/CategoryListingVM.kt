package com.example.mva_sugarcounter.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.example.mva_sugarcounter.data.AppDatabase
import com.example.mva_sugarcounter.data.Category
import com.example.mva_sugarcounter.data.states.CategoryListingStates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryListingVM(private val application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(this.getApplication())

    //SateFlows: START
    val _categoryListShown = MutableStateFlow(false)
    val categoryListShown = _categoryListShown.asStateFlow()

    private val _categories = MutableStateFlow(emptyMap<Char, List<Category>>())
    val categories = _categories.asStateFlow()

    val _categoryDeleteAlertDialog = MutableStateFlow(false)
    val categoryDeleteAlertDialog = _categoryDeleteAlertDialog.asStateFlow()

    val _categoryDeleteId = MutableStateFlow(0)
    val categoryDeleteId = _categoryDeleteId.asStateFlow()

    val _deletionCheckboxes = MutableStateFlow(CategoryListingStates())
    val deletionCheckboxes = _deletionCheckboxes.asStateFlow()

    val _deletionList = MutableStateFlow(mutableListOf(0))
    val deletionList = _deletionList.asStateFlow()
    //SateFlows: END


    //Observer _categories: START
    // Observer that is used to observe Dao of RoomDB
    private val categoriesObserver = Observer<List<Category>> { it ->
        val sortedCategories = it.groupBy(keySelector = { it.category.first() }).toSortedMap()
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
    fun actionShowCategories() {
        _categoryListShown.value = true
    }

    fun actionHideCategories() {
        _categoryListShown.value = false
    }

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

    fun actionChangeDeleteStatus(id: Int) {
        if (_deletionList.value.contains(id)) {
            _deletionList.value.drop(id)
        } else {
            _deletionList.value.add(id)
        }
    }
    //Actions: END


}