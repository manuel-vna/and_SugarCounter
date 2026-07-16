package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.api.networkModule
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.di.appModule
import com.jumparoundcreations.mva_sugarcounter.main.MainScreenView
import com.jumparoundcreations.mva_sugarcounter.ui.theme.SugarCounterTheme
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class CreateNewEntryTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: AppDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        stopKoin() // Ensure a clean state
        startKoin {
            androidContext(context)
            allowOverride(true)
            modules(appModule, module {
                single<AppDatabase> { db }
                single<SharedPreferences> { context.getSharedPreferences("TEST_PREFS", Context.MODE_PRIVATE) }

                },
                networkModule
            )
        }
    }

    @After
    fun tearDown() {
        db.close()
        stopKoin()
    }

    @Test
    fun createEntryForToday() {
        val categoryName = "Chocolate"
        val sugarPer100g = "39.3"
        val consumedAmount = "50"
        
        // The total is displayed as "19.7g" (exact match on the row) 
        // OR inside the total summary as "total: 19.7" (depending on locale)
        val expectedSubstring = "19."
        val gramUnit = context.getString(R.string.gram_unit_short) // "g"

        composeTestRule.setContent {
            SugarCounterTheme(dynamicColor = false) {
                MainScreenView(context = context, showNavigationRail = false)
            }
        }

        // 1. Fill fields
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.accessibility_category_input_field))
            .performTextInput(categoryName)
        
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.accessibility_perHundredGram_textField))
            .performTextInput(sugarPer100g)

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.accessibility_perHundredGram_textField_consumed))
            .performTextInput(consumedAmount)

        // 2. Press Save
        composeTestRule.onNodeWithText(context.getString(R.string.saveButton)).performClick()

        // 3. Verify entry in the list
        // We use waitUntil because the save operation is asynchronous and might take a moment to reflect in the UI.
        // We check for the category name first to ensure the row has appeared.
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText(categoryName).fetchSemanticsNodes().isNotEmpty()
        }
        
        // We look for "19." as a substring. 
        // Note: It appears twice (in the entry row and in the day's total summary).
        // We verify that both instances exist by asserting the count is 2.
        composeTestRule.onAllNodesWithText(expectedSubstring, substring = true).assertCountEquals(2)
        composeTestRule.onAllNodesWithText(gramUnit, substring = true).fetchSemanticsNodes().isNotEmpty()

        // 4. Verify category on Category Screen
        // We use useUnmergedTree = true because the NavigationBarItem merges its descendants 
        // (Icon + Text), and the content description might be on the internal Icon node.
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.categoryPlural), useUnmergedTree = true)
            .performClick()
        composeTestRule.onNodeWithText(categoryName).assertExists()
    }

    // TODO: Further test scenarios
    // fun entryWithGramPerUnit
    // fun entryWithPresentDate()
    // fun entryWithAlreadyExistingCategory()
    // Unhappy paths: Wrong input tests

}
