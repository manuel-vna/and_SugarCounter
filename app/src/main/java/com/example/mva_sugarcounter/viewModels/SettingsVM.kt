package com.example.mva_sugarcounter.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.mva_sugarcounter.data.AppDatabase
import com.example.mva_sugarcounter.data.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsVM(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(this.getApplication())


    //SateFlows: START
    val _settingsScreenShown = MutableStateFlow(true)
    val settingsScreenShown = _settingsScreenShown.asStateFlow()

    val _categoryListShown = MutableStateFlow(false)
    val categoryListShown = _categoryListShown.asStateFlow()

    private val _categories = MutableStateFlow(listOf<Category>())
    val categories = _categories.asStateFlow()

    val _categoryDeleteAlertDialog = MutableStateFlow(false)
    val categoryDeleteAlertDialog = _categoryDeleteAlertDialog.asStateFlow()

    val _categoryDeleteId = MutableStateFlow(0)
    val categoryDeleteId = _categoryDeleteId.asStateFlow()
    //SateFlows: END


    //Observer _categories: START
    // Observer that is used to observe Dao of RoomDB
    private val categoriesObserver = Observer<List<Category>> {
        val sortedCategories = it.sortedBy { it.category }
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
        _settingsScreenShown.value = false
    }

    fun actionHideCategories() {
        _categoryListShown.value = false
        _settingsScreenShown.value = true
    }

    fun actionShowDeleteAlertDialog(categoryId: Int) {
        _categoryDeleteAlertDialog.value = true
        _categoryDeleteId.value = categoryId
    }

    fun actionDismissAlertDialog() {
        _categoryDeleteAlertDialog.value = false
    }

    fun actionDeleteCategory(categoryId: Int) {
        _categoryDeleteAlertDialog.value = false
        viewModelScope.launch(Dispatchers.IO) {
            database.appDao().deleteSpecificCategory(categoryId)
        }
    }
    //Actions: END

}