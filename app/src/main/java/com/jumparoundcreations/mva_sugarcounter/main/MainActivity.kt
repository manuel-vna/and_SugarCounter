package com.jumparoundcreations.mva_sugarcounter.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.ui.theme.SugarCounterTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedPrefsMain by inject<SharedPreferences>()
    private var isDynamicColor by mutableStateOf(
        sharedPrefsMain.getBoolean(
            "dynamicColorActivated",
            false
        )
    )

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String?
    ) {
        if (key == "dynamicColorActivated") {
            isDynamicColor = sharedPrefsMain.getBoolean(
                "dynamicColorActivated",
                false
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        sharedPrefsMain.registerOnSharedPreferenceChangeListener(this)

        setContent {
            SugarCounterTheme(dynamicColor = isDynamicColor) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenView(applicationContext)
                }
            }
        }
        val appDatabase = AppDatabase.getInstance(applicationContext)
        testing(appDatabase)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPrefsMain.unregisterOnSharedPreferenceChangeListener(this)
    }

}

@OptIn(DelicateCoroutinesApi::class)
fun testing(appDatabase: AppDatabase) {
    GlobalScope.launch(Dispatchers.IO) {

        val deletionPointInTime = (System.currentTimeMillis() / 1000) - 86400

        // SugarEntries: START
        val categoriesOfSugarEntriesToBeDeleted = appDatabase.appDao()
            .getCategoriesOfSugarEntriesToBeDeleted(deletionPointInTime)
        println("categoriesOfSugarEntriesToBeDeleted: " + categoriesOfSugarEntriesToBeDeleted)

        val categoryExistsInMoreThanOneEntryRow = categoriesOfSugarEntriesToBeDeleted.map {
            Pair(
                first = it,
                second = appDatabase.appDao()
                    .checkIfCategoryIsPresentSinceInSugarTable(it, deletionPointInTime)
            )
        }
        println("categoryExistsInMoreThanOneEntryRow: " + categoryExistsInMoreThanOneEntryRow)

        val categoriesToBeDeletedFromSugarEntries = categoryExistsInMoreThanOneEntryRow.filter {
            it.second.not()
        }.map { it.first }
        println("categoriesToBeDeletedFromSugarEntries: " + categoriesToBeDeletedFromSugarEntries)
        // SugarEntries: END

        //CaloriesEntries: START
        val categoriesOfCaloriesEntriesToBeDeleted = appDatabase.appDao()
            .getCategoriesOfCaloriesEntriesToBeDeleted(deletionPointInTime)
        println("categoriesOfCaloriesEntriesToBeDeleted: " + categoriesOfCaloriesEntriesToBeDeleted)

        val categoryExistsInMoreThanOneCaloriesRow = categoriesOfCaloriesEntriesToBeDeleted.map {
            Pair(
                first = it,
                second = appDatabase.appDao()
                    .checkIfCategoryIsPresentSinceInCaloriesTable(it, deletionPointInTime)
            )
        }
        println("categoryExistsInMoreThanOneCaloriesRows: " + categoryExistsInMoreThanOneCaloriesRow)

        val categoriesToBeDeletedFromCaloriesEntries =
            categoryExistsInMoreThanOneCaloriesRow.filter {
                it.second.not()
            }.map { it.first }
        println("categoriesToBeDeletedFromCaloriesEntries: " + categoriesToBeDeletedFromCaloriesEntries)
        //CaloriesEntries: END

        val categoriesToBeDeletedOverall =
            categoriesToBeDeletedFromSugarEntries + categoriesToBeDeletedFromCaloriesEntries
        println(categoriesToBeDeletedOverall)

    }
}
