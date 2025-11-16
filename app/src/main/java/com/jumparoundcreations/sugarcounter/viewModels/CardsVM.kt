package com.jumparoundcreations.sugarcounter.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.data.counterData.GramCountMode
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.util.HelperMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CardsVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()

    //StateFlow: START

    private val _showCardItemBottomSheet = MutableStateFlow(false)
    val showCardItemBottomSheet = _showCardItemBottomSheet.asStateFlow()

    private val _cardItemToShowIsEntrySugar = MutableStateFlow(false)
    val cardItemToShowIsEntrySugar = _cardItemToShowIsEntrySugar.asStateFlow()

    private val _cardItemToShowSugar = MutableStateFlow(
        SugarEntry(
            0, 0, "", "", GramCountMode.PerHundred, 0.0, 0.0, 0.0
        )
    )
    val cardItemToShowSugar = _cardItemToShowSugar.asStateFlow()

    private val _valueCategory = MutableStateFlow("")
    val valueCategory = _valueCategory.asStateFlow()

    private val _headingProportion = MutableStateFlow("")
    val headingProportion = _headingProportion.asStateFlow()

    private val _headingConsumed = MutableStateFlow("")
    val headingConsumed = _headingConsumed.asStateFlow()

    private val _valueProportion = MutableStateFlow("")
    val valueProportion = _valueProportion.asStateFlow()

    private val _valueConsumed = MutableStateFlow("")
    val valueConsumed = _valueConsumed.asStateFlow()

    private val _dialogEntryDeletionConfirmation = MutableStateFlow(false)
    val dialogEntryDeletionConfirmation = _dialogEntryDeletionConfirmation.asStateFlow()

    //StateFlow: END


    //Actions: START

    fun actionShowCardItem(item: SugarEntry) {
        _cardItemToShowIsEntrySugar.value = true
        _cardItemToShowSugar.value = item
        _showCardItemBottomSheet.value = true
    }

    fun actionDismissCardItem() {
        _showCardItemBottomSheet.value = false
    }

    fun actionChangeValueCategory(newString: String) {
        _valueCategory.value = newString
    }

    fun actionChangeHeadingProportion(newHeadingProportion: String) {
        _headingProportion.value = newHeadingProportion
    }

    fun actionChangeHeadingConsumed(newHeadingConsumed: String) {
        _headingConsumed.value = newHeadingConsumed
    }

    fun actionChangeValueProportion(newValueProportion: String) {
        _valueProportion.value = newValueProportion
    }

    fun actionChangeValueConsumed(newValueConsumed: String) {
        _valueConsumed.value = newValueConsumed
    }

    /*
    fun actionEditDatabaseEntry(
        isEntrySugar: Boolean,
        entryPerHundred: EntryPerHundred,
        entryPerPiece: EntryPerPiece,
        valueCategory: String,
        valueProportion: String,
        valueConsumed: String
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            when (isEntrySugar) {
                true -> if (entrySugar.isPerHundred) {
                    database.appDao().updateEntrySugarPerHundred(
                        entrySugar.id,
                        valueProportion.toInt(),
                        valueConsumed.toInt(),
                        gramTotal = ((valueProportion.toDouble() / 100) * valueConsumed.toDouble()).roundToInt()
                    )
                } else {
                    database.appDao().updateEntrySugarPerPiece(
                        entrySugar.id,
                        valueProportion.toInt(),
                        valueConsumed.toInt(),
                        gramTotal = valueProportion.toInt() * valueConsumed.toInt()
                    )
                }

                else -> {
                    database.appDao().updateEntryCalories(
                        entryCalories.id,
                        valueProportion.toInt(),
                        valueConsumed.toInt(),
                        caloriesTotal = valueProportion.toInt() * valueConsumed.toInt()
                    )
                }
            }

            if (valueCategory != entrySugar.category) {

                database.appDao().updateEntrySugarCategoryOfLastXDays(
                    oldCategory = entrySugar.category,
                    newCategory = valueCategory.trim(),
                    startPoint = AppConstants.endOf90DaysAgo,
                    endPoint = AppConstants.endOfToday
                )

                try {
                    database.appDao().updateCategoryOnEdit(
                        oldCategory = if (isEntrySugar) entrySugar.category.trim() else entryCalories.category.trim(),
                        newCategory = valueCategory.trim()
                    )
                } catch (exception: SQLiteConstraintException) {
                    database.appDao().deleteSpecificCategoryByName(entrySugar.category)
                    Log.v("SugarCounter", "Category already exists in database: $exception")
                }
            }
        }
    }
    */

    fun actionDeleteSpecificEntryRow(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            database.appDao().deleteSpecificEntryRow(id)
        }
    }

    fun actionShowDialogEntryDeletionConfirmation(isShown: Boolean) {
        _dialogEntryDeletionConfirmation.value = isShown
    }

    fun actionReuseEntryForToday(entrySugar: SugarEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTimestamp = System.currentTimeMillis() / 1000

            if (entrySugar.entryType == GramCountMode.PerHundred) {
                database.appDao().insertSugarEntry(
                    SugarEntry(
                        currentTimestamp = currentTimestamp,
                        date = HelperMethods.convertTimestampToDateString(
                            currentTimestamp,
                            "yyyy-MM-dd"
                        ),
                        category = entrySugar.category,
                        entryType = GramCountMode.PerHundred,
                        gram = entrySugar.gram,
                        quantity = entrySugar.quantity,
                        gramTotal = entrySugar.gramTotal
                    )
                )
            } else {
                database.appDao().insertSugarEntry(
                    SugarEntry(
                        currentTimestamp = currentTimestamp,
                        date = HelperMethods.convertTimestampToDateString(
                            currentTimestamp,
                            "yyyy-MM-dd"
                        ),
                        category = entrySugar.category,
                        entryType = GramCountMode.PerPiece,
                        gram = entrySugar.gram,
                        quantity = entrySugar.quantity,
                        gramTotal = entrySugar.gramTotal
                    )
                )
            }
        }
    }

//Actions: END

}



