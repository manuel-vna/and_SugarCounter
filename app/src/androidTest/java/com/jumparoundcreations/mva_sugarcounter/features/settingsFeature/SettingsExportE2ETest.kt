package com.jumparoundcreations.mva_sugarcounter.features.settingsFeature

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.features.settingsFeature.useCases.ExportEntriesUseCase
import com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI.SettingsUI
import com.jumparoundcreations.mva_sugarcounter.ui.theme.SugarCounterTheme
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.io.InputStreamReader

@RunWith(AndroidJUnit4::class)
class SettingsExportE2ETest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: AppDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        
        // 1. Setup in-memory database
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        // 2. Setup Koin for DI used by SettingsUI and SettingsVM
        stopKoin() // Ensure a clean state
        startKoin {
            androidContext(context)
            modules(module {
                single { db }
                single { context.getSharedPreferences("TEST_PREFS", Context.MODE_PRIVATE) }
                single { ExportEntriesUseCase() }
                viewModel { SettingsVM(get()) }
            })
        }

        // 3. Pre-populate database with a test entry using correct DAO method
        db.appDao().insertSugarEntry(
            SugarEntry(
                currentTimestamp = System.currentTimeMillis(),
                date = "2023-10-27_10:00",
                category = "TestCategory",
                entryType = GramCountMode.PerHundred,
                gram = 10.0,
                quantity = 2.0,
                gramTotal = 20.0
            )
        )
    }

    @After
    fun tearDown() {
        db.close()
        stopKoin()
    }

    @Test
    fun exportProcess_createsCsvFileWithCorrectContent() {
        // This test is specifically designed for Android R+ (API 30+) as requested
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        val exportButtonLabel = context.getString(R.string.export_settings_button)

        // 1. Start SettingsUI
        composeTestRule.setContent {
            SugarCounterTheme(dynamicColor = false) {
                val navController = rememberNavController()
                SettingsUI(context = context, navController = navController)
            }
        }

        // 2. Click "Export entries" in SettingsUI (This opens the bottom sheet)
        composeTestRule.onNodeWithText(exportButtonLabel).performClick()

        // 3. Click the button on the Bottom Sheet that starts the export
        // We wait for the specific clickable button in the bottom sheet to appear.
        // It has trailing spaces "Export entries   " and a click action.
        val exportBtnMatcher = hasText(exportButtonLabel, substring = true) and hasClickAction()
        
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodes(exportBtnMatcher).fetchSemanticsNodes().isNotEmpty()
        }
        
        val nodes = composeTestRule.onAllNodes(exportBtnMatcher)
        nodes[nodes.fetchSemanticsNodes().size - 1].performClick()


        // 4. Wait for export to finish (BottomSheet with success message should appear)
        val successText = context.getString(R.string.export_bottom_sheet_success)
        val errorText = context.getString(R.string.general_error)
        
        composeTestRule.waitUntil(60000) {
            val successVisible = composeTestRule.onAllNodes(hasText(successText)).fetchSemanticsNodes().isNotEmpty()
            val errorVisible = composeTestRule.onAllNodes(hasText(errorText)).fetchSemanticsNodes().isNotEmpty()
            successVisible || errorVisible
        }

        // Check if it was an error instead of success
        val errorNodes = composeTestRule.onAllNodes(hasText(errorText)).fetchSemanticsNodes()
        if (errorNodes.isNotEmpty()) {
            throw AssertionError("Export failed: Error message is visible on screen")
        }

        // 5. Verify the file in Downloads via MediaStore
        verifyExportedFileContent("TestCategory", "20.0")

    }

    private fun verifyExportedFileContent(expectedCategory: String, expectedTotal: String) {
        // Give MediaStore a moment to finish indexing
        Thread.sleep(2000)

        val projection = arrayOf(
            MediaStore.Downloads._ID,
            MediaStore.Downloads.DISPLAY_NAME,
            MediaStore.Downloads.DATE_ADDED
        )
        
        // Query for files starting with "sugarCounter-"
        val selection = "${MediaStore.Downloads.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("sugarCounter-%")
        val sortOrder = "${MediaStore.Downloads.DATE_ADDED} DESC"

        var fileFound = false
        val startTime = System.currentTimeMillis()
        
        // Retry querying MediaStore for up to 10 seconds as indexing can be async
        while (System.currentTimeMillis() - startTime < 10000) {
            context.contentResolver.query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME))
                    val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI.buildUpon()
                        .appendPath(id.toString()).build()

                    context.contentResolver.openInputStream(contentUri)?.use { inputStream ->
                        val content = InputStreamReader(inputStream).readText()
                        
                        // Verify CSV content
                        assertTrue("CSV file '$name' should contain category: $expectedCategory", content.contains(expectedCategory))
                        assertTrue("CSV file '$name' should contain gramTotal: $expectedTotal", content.contains(expectedTotal))
                        assertTrue("CSV file '$name' should contain header", content.contains("Date") && content.contains("Name") && content.contains("Mode"))
                        fileFound = true
                    }
                }
            }
            if (fileFound) break
            Thread.sleep(1000)
        }
        
        if (!fileFound) {
            throw AssertionError("No exported CSV file found in Downloads after 10 seconds")
        }
    }
}
