package com.jumparoundcreations.sugarcounter.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.ui.theme.SugarCounterTheme
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

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        sharedPrefsMain.registerOnSharedPreferenceChangeListener(this)

        setContent {
            SugarCounterTheme(dynamicColor = isDynamicColor) {
                // A surface container using the 'background' color from the theme

                val windowClass = calculateWindowSizeClass(activity = this)
                val showNavigationRail =
                    windowClass.widthSizeClass != WindowWidthSizeClass.Compact

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MainScreenView(applicationContext, showNavigationRail)
                }
            }
        }
        val appDatabase = AppDatabase.getInstance(applicationContext)
        //testing(appDatabase)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPrefsMain.unregisterOnSharedPreferenceChangeListener(this)
    }

}

@OptIn(DelicateCoroutinesApi::class)
fun testing(appDatabase: AppDatabase) {
    GlobalScope.launch(Dispatchers.IO) {

        //86400 = one day in seconds, 172800 = 2 days in seconds
        val deletionPointInTime = (System.currentTimeMillis() / 1000) - 172800

        //<editor-fold desc="SugarEntries">
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

        val categoriesToBeDeletedFromSugarEntries =
            categoryExistsInMoreThanOneEntryRow.filter {
                it.second.not()
            }.map { it.first }
        println("categoriesToBeDeletedFromSugarEntries: " + categoriesToBeDeletedFromSugarEntries)
        //</editor-fold>

        //<editor-fold desc="CaloriesEntries">
        val categoriesOfCaloriesEntriesToBeDeleted = appDatabase.appDao()
            .getCategoriesOfCaloriesEntriesToBeDeleted(deletionPointInTime)
        println("categoriesOfCaloriesEntriesToBeDeleted: " + categoriesOfCaloriesEntriesToBeDeleted)

        val categoryExistsInMoreThanOneCaloriesRow =
            categoriesOfCaloriesEntriesToBeDeleted.map {
                Pair(
                    first = it,
                    second = appDatabase.appDao()
                        .checkIfCategoryIsPresentSinceInCaloriesTable(
                            it,
                            deletionPointInTime
                        )
                )
            }
        println("categoryExistsInMoreThanOneCaloriesRows: " + categoryExistsInMoreThanOneCaloriesRow)

        val categoriesToBeDeletedFromCaloriesEntries =
            categoryExistsInMoreThanOneCaloriesRow.filter {
                it.second.not()
            }.map { it.first }
        println("categoriesToBeDeletedFromCaloriesEntries: " + categoriesToBeDeletedFromCaloriesEntries)
        //</editor-fold>

        //<editor-fold desc="Deletion of Categories">
        val categoriesToBeDeletedOverall =
            categoriesToBeDeletedFromSugarEntries + categoriesToBeDeletedFromCaloriesEntries
        println(categoriesToBeDeletedOverall)

        println("Deleted categories:")
        categoriesToBeDeletedOverall.forEach {
            appDatabase.appDao().deleteSpecificCategoryByName(it)
            println(it)
        }
        //</editor-fold>

        //<editor-fold desc="Deletion of Sugar Entries">
        appDatabase.appDao().deleteEntriesSugarOlderThanN(deletionPointInTime)
        println("Sugar Entries were deleted")
        //</editor-fold>

        //<editor-fold desc="Deletion of Calories Entries">
        appDatabase.appDao().deleteEntriesCaloriesOlderThanN(deletionPointInTime)
        println("Calories Entries were deleted")
        //</editor-fold>

    }
}
