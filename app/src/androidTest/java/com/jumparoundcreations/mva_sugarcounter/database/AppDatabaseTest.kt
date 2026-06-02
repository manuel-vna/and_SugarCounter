package com.jumparoundcreations.mva_sugarcounter.database

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    /*
    Table name of table that is tested: calories_table
    Mandatory for the test are both json schemes, the origin and the target scheme of the migration.
    These schemes need to be generated first by adding the migration to the main app and building the app
    Also check within the JSON files that both schemes have the expected fields
     */

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper =
        MigrationTestHelper(
            // Instrumentation allows test cases to interact with app components like databases, activities, services, and UI.
            instrumentation = InstrumentationRegistry.getInstrumentation(),
            databaseClass = AppDatabase::class.java,
            // Specs is a list of AutoMigrationSpec implementations.
            specs = emptyList(),
            // OpenFactory creates an SQLite database instance that Room will use inside the test environment.
            openFactory = FrameworkSQLiteOpenHelperFactory(),
        )
    private val MIGRATION_8_9 =
        object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE calories_table ADD COLUMN caloriesPerPiece INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE calories_table ADD COLUMN caloriesAmount INTEGER NOT NULL DEFAULT 1")
            }
        }

    @Test
    @Throws(IOException::class)
    fun migrate8To9() {
        // Step 1: Create the database at version X
        val db = helper.createDatabase(TEST_DB, 8)
        db.execSQL(
            "INSERT INTO calories_table (currentTimestamp, date, category, caloriesTotal) VALUES (1740250521, '2025-02-22', 'Pizza', 800)",
        )

        // Step 1.1: Check column count
        val cursorOne = db.query("PRAGMA table_info(calories_table)")
        val columnCountBefore = cursorOne.count
        cursorOne.close()

        db.close()

        val dbMigrated = helper.runMigrationsAndValidate(TEST_DB, 9, true, MIGRATION_8_9)

        // Step 2: Query to verify that the migration worked
        val cursor = dbMigrated.query("SELECT * FROM calories_table")
        cursor.moveToFirst()

        // Step 3: Check if indexes are correct
        val currentTimestamp = cursor.getColumnIndex("currentTimestamp")
        assertTrue(currentTimestamp != -1)
        val category = cursor.getColumnIndex("category")
        assertTrue(category != -1)
        val caloriesPerPiece = cursor.getColumnIndex("caloriesPerPiece")
        assertTrue(caloriesPerPiece != -1)
        val caloriesAmount = cursor.getColumnIndex("caloriesAmount")
        assertTrue(caloriesAmount != -1) // Ensure the column exists
        assertEquals(1, cursor.getInt(caloriesAmount))
        cursor.close()

        // Step 4: Check column count
        val cursorTwo = dbMigrated.query("PRAGMA table_info(calories_table)")
        val columnCountAfter = cursorTwo.count
        cursorTwo.close()

        // Step 5: Verify the column count before and after migration
        assertEquals(5, columnCountBefore)
        assertEquals(7, columnCountAfter)
    }
}
