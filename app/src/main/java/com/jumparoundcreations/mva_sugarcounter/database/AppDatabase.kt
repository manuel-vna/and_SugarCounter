package com.jumparoundcreations.mva_sugarcounter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.Category
import com.jumparoundcreations.mva_sugarcounter.util.CustomTypeConverter

@Database(entities = [SugarEntry::class, Category::class], version = 12)
@TypeConverters(CustomTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): DaoAppDatabase

    companion object {
        private val migration_8_9 =
            object : Migration(8, 9) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE calories_table ADD COLUMN caloriesPerPiece INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE calories_table ADD COLUMN caloriesAmount INTEGER NOT NULL DEFAULT 1")
                    db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_category_table_category` ON `category_table` (`category`)")
                }
            }
        val migration_10_11 =
            object : Migration(10, 11) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // Create the new "sugarEntriesTable"
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS sugarEntriesTable (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            date TEXT NOT NULL,
                            currentTimestamp INTEGER NOT NULL,
                            category TEXT NOT NULL,
                            entryType TEXT NOT NULL,
                            gram REAL NOT NULL,
                            quantity REAL NOT NULL,
                            gramTotal REAL NOT NULL
                        )
                        """.trimIndent(),
                    )

                    // Insert ALL rows in correct original order
                    db.execSQL(
                        """
                        INSERT INTO sugarEntriesTable (
                            currentTimestamp,
                            date,
                            category,
                            entryType,
                            gram,
                            quantity,
                            gramTotal
                        )
                        SELECT
                            currentTimestamp,
                            date,
                            category,
                            CASE WHEN isPerHundred = 1 THEN 'PerHundred' ELSE 'PerPiece' END AS entryType,
                            CASE WHEN isPerHundred = 1 THEN perHundredGram * 1.0 ELSE perPieceGram * 1.0 END AS gram,
                            CASE WHEN isPerHundred = 1 THEN perHundredQuantity * 1.0 ELSE perPieceAmount * 1.0 END AS quantity,
                            gramTotal * 1.0
                        FROM entry_table
                        ORDER BY currentTimestamp ASC
                        """.trimIndent(),
                    )

                    // Drop the old entry_table now that data has been migrated
                    db.execSQL("DROP TABLE entry_table")

                    // Drop the calories table since this feature is discontinued
                    // db.execSQL("DROP TABLE entry_calories")
                }
            }

        val migration_11_12 =
            object : Migration(11, 12) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // Rename the old table to avoid conflicts
                    db.execSQL("ALTER TABLE sugarEntriesTable RENAME TO sugarEntriesTable_old")

                    // Create the new "sugarEntriesTable"
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS sugarEntriesTable (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            currentTimestamp INTEGER NOT NULL,
                            date TEXT NOT NULL,
                            category TEXT NOT NULL,
                            entryType TEXT NOT NULL,
                            gramPerHundred REAL,
                            gramPerPiece REAL,
                            quantity REAL,
                            amount REAL,
                            gramTotal REAL NOT NULL
                        )
                        """.trimIndent(),
                    )

                    // Insert ALL rows from the renamed old table
                    db.execSQL(
                        """
                        INSERT INTO sugarEntriesTable (
                            currentTimestamp,
                            date,
                            category,
                            entryType,
                            gramPerHundred,
                            gramPerPiece,
                            quantity,
                            amount,
                            gramTotal
                        )
                        SELECT
                            currentTimestamp,
                            date,
                            category,
                            entryType,
                            CASE WHEN entryType = 'PerHundred' THEN gram ELSE NULL END,
                            CASE WHEN entryType = 'PerPiece' THEN gram ELSE NULL END,
                            CASE WHEN entryType = 'PerHundred' THEN quantity ELSE NULL END,
                            CASE WHEN entryType = 'PerPiece' THEN quantity ELSE NULL END,
                            gramTotal
                        FROM sugarEntriesTable_old
                        ORDER BY currentTimestamp ASC
                        """.trimIndent(),
                    )

                    // Drop the old table now that data has been migrated
                    db.execSQL("DROP TABLE sugarEntriesTable_old")
                }
            }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(application: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance =
                        Room
                            .databaseBuilder(
                                application.applicationContext,
                                AppDatabase::class.java,
                                "app_database",
                            ).addMigrations(migration_8_9, migration_10_11, migration_11_12)
                            .fallbackToDestructiveMigration(false)
                            .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
