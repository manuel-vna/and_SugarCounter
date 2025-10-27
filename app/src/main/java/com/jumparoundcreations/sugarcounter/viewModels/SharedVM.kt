package com.jumparoundcreations.sugarcounter.viewModels

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jumparoundcreations.sugarcounter.data.AppConstants
import com.jumparoundcreations.sugarcounter.data.Entry
import com.jumparoundcreations.sugarcounter.data.EntryCalories
import com.jumparoundcreations.sugarcounter.data.IEntry
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.util.HelperMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToInt

class SharedVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()

    //StateFlow: START

    private val _showCardItemBottomSheet = MutableStateFlow(false)
    val showCardItemBottomSheet = _showCardItemBottomSheet.asStateFlow()

    private val _cardItemToShowIsEntrySugar = MutableStateFlow(false)
    val cardItemToShowIsEntrySugar = _cardItemToShowIsEntrySugar.asStateFlow()

    private val _cardItemToShowSugar = MutableStateFlow(
        Entry(
            0, 0, "", "", true, 0, 0, 0, 0, 0
        )
    )
    val cardItemToShowSugar = _cardItemToShowSugar.asStateFlow()

    private val _cardItemToShowCalories = MutableStateFlow(
        EntryCalories(
            0, 0, "", "", 0, 1, 0
        )
    )
    val cardItemToShowCalories = _cardItemToShowCalories.asStateFlow()

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

    fun actionShowCardItem(item: IEntry) {
        when (item) {
            is Entry -> {
                _cardItemToShowIsEntrySugar.value = true
                _cardItemToShowSugar.value = item
            }

            is EntryCalories -> {
                _cardItemToShowIsEntrySugar.value = false
                _cardItemToShowCalories.value = item
            }

            else -> Log.d("CardDetailsSheet", "Showing CardDetailsSheet did not work")
        }
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

    fun actionEditDatabaseEntry(
        isEntrySugar: Boolean,
        entrySugar: Entry,
        entryCalories: EntryCalories,
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

            if (valueCategory != entrySugar.category && valueCategory != entryCalories.category) {

                database.appDao().updateEntrySugarCategoryOfLastXDays(
                    oldCategory = if (isEntrySugar) entrySugar.category else entryCalories.category,
                    newCategory = valueCategory.trim(),
                    startPoint = AppConstants.endOf90DaysAgo,
                    endPoint = AppConstants.endOfToday
                )

                database.appDao().updateEntryCaloriesCategoryOfLastXDays(
                    oldCategory = if (isEntrySugar) entrySugar.category else entryCalories.category,
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

    fun actionDeleteSpecificEntryRow(
        itemToDeleteIsEntrySugar: Boolean, id: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (itemToDeleteIsEntrySugar) {
                database.appDao().deleteSpecificEntryRow(id)
            } else {
                database.appDao().deleteSpecificEntryCaloriesRow(id)
            }
        }
    }

    fun actionShowDialogEntryDeletionConfirmation(isShown: Boolean) {
        _dialogEntryDeletionConfirmation.value = isShown
    }

    fun actionReuseEntryForToday(
        isEntrySugar: Boolean,
        entrySugar: Entry,
        entryCalories: EntryCalories
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            val currentTimestamp = System.currentTimeMillis() / 1000

            when (isEntrySugar) {
                true ->
                    if (entrySugar.isPerHundred) {
                        database.appDao().insertEntry(
                            Entry(
                                currentTimestamp = currentTimestamp,
                                date = HelperMethods.convertTimestampToDateString(
                                    currentTimestamp,
                                    "yyyy-MM-dd"
                                ),
                                category = entrySugar.category,
                                isPerHundred = true,
                                perPieceGram = 0,
                                perPieceAmount = 0,
                                perHundredGram = entrySugar.perHundredGram,
                                perHundredQuantity = entrySugar.perHundredQuantity,
                                gramTotal = entrySugar.gramTotal
                            )
                        )
                    } else {
                        database.appDao().insertEntry(
                            Entry(
                                currentTimestamp = currentTimestamp,
                                date = HelperMethods.convertTimestampToDateString(
                                    currentTimestamp,
                                    "yyyy-MM-dd"
                                ),
                                category = entrySugar.category,
                                isPerHundred = false,
                                perPieceGram = entrySugar.perPieceGram,
                                perPieceAmount = entrySugar.perPieceAmount,
                                perHundredGram = 0,
                                perHundredQuantity = 0,
                                gramTotal = entrySugar.gramTotal
                            )
                        )
                    }

                else -> {
                    database.appDao().insertEntryCalories(
                        EntryCalories(
                            currentTimestamp = currentTimestamp,
                            date = HelperMethods.convertTimestampToDateString(
                                currentTimestamp,
                                "yyyy-MM-dd"
                            ),
                            category = entryCalories.category,
                            caloriesPerPiece = entryCalories.caloriesPerPiece,
                            caloriesAmount = entryCalories.caloriesAmount,
                            caloriesTotal = entryCalories.caloriesTotal
                        )
                    )
                }
            }
        }
    }

//Actions: END

}



