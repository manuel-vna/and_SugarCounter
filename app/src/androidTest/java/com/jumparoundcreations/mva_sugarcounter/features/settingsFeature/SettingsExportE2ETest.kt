package com.jumparoundcreations.mva_sugarcounter.features.settingsFeature

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsExportE2ETest {

    private lateinit var db: AppDatabase
    private lateinit var dao: DaoAppDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        // <editor-fold desc="Setup Room test database with a test entry">
        db =
            Room
                .inMemoryDatabaseBuilder(
                    context,
                    AppDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
        dao = db.appDao()

        dao.insertSugarEntry(
            SugarEntry(
                id = 0,
                currentTimestamp = 123L,
                date = "2005-05-05",
                category = "Duplo",
                entryType = GramCountMode.PerPiece,
                gram = 9.0,
                quantity = 1.0,
                gramTotal = 9.0,
            )
        )
        // </editor-fold>
    }



}


/*
@Test
fun testExportFeature() = runTest {
    // 1. Setup: Insert some dummy data into the database
    database.appDao().insertEntry(testEntry)

    // 2. UI: Navigate to Settings and click Export
    composeTestRule.onNodeWithText("Export").performClick()

    // 3. UI: Handle Permission if necessary (UI Automator)
    // ... code to click "Allow" ...

    // 4. Synchronization: Wait for the Success Bottom Sheet
    composeTestRule.waitUntil(timeoutMillis = 5000) {
        composeTestRule.onAllNodesWithText("Export successful").fetchSemanticsNodes().isNotEmpty()
    }

    // 5. Verification: Check MediaStore or File System
    val resolver = context.contentResolver
    val uri = ExportData.uri // Since ExportData is a singleton, you can access the URI directly in-process

    Assert.assertNotNull(uri)
    resolver.openInputStream(uri!!).use { input ->
        val content = input?.bufferedReader()?.readText()
        Assert.assertTrue(content!!.contains("Date,Name,Mode")) // Check header
        Assert.assertTrue(content.contains("Test Category"))   // Check data
    }
}
 */