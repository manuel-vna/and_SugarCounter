package com.jumparoundcreations.mva_sugarcounter.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class CounterCaloriesVM : ViewModel(), KoinComponent {

    private val database by inject<AppDatabase>()
    private val sharedPrefsMain by inject<SharedPreferences>(qualifier = named("sharedPrefsMain"))


    //StateFlow: START
    var _caloriesInput = MutableStateFlow("")
    val caloriesInput = _caloriesInput.asStateFlow()

    //StateFlow: END

    //Actions: Start
    fun actionPerPieceGramChange(caloriesInKcal: String) {
        _caloriesInput.value = caloriesInKcal
    }
    //Actions: End

}