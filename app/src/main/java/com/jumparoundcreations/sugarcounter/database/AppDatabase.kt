package com.jumparoundcreations.sugarcounter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jumparoundcreations.sugarcounter.data.Entry
import com.jumparoundcreations.sugarcounter.data.EntryCalories
import com.jumparoundcreations.sugarcounter.data.categoryData.Category
import com.jumparoundcreations.sugarcounter.util.CustomTypeConverter

@Database(entities = [Entry::class, EntryCalories::class, Category::class], version = 10)
@TypeConverters(CustomTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): DaoAppDatabase

    companion object {

        val migration_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {

                // Create the new "entryPerHundred_table"
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS entryPerHundred_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date TEXT NOT NULL,
                currentTimestamp INTEGER NOT NULL,
                category TEXT NOT NULL,
                entryType TEXT NOT NULL,
                perHundredGram REAL NOT NULL,
                perHundredQuantity REAL NOT NULL,
                perPieceGram REAL NOT NULL
            )
        """.trimIndent()
                )

                // Create the new "entryPerPiece_table"
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS entryPerPiece_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date TEXT NOT NULL,
                currentTimestamp INTEGER NOT NULL,
                category TEXT NOT NULL,
                entryType TEXT NOT NULL,
                perPieceGram REAL NOT NULL,
                perPieceAmount REAL NOT NULL,
                gramTotal REAL NOT NULL
            )
        """.trimIndent()
                )

                // Move "isPerHundred == 1" rows → entryPerHundred_table
                // Convert integer columns to REAL using *1.0
                // Assign entryType = 'PerHundred'
                database.execSQL(
                    """
            INSERT INTO entryPerHundred_table (
                date,
                currentTimestamp,
                category,
                entryType,
                perHundredGram,
                perHundredQuantity,
                perPieceGram
            )
            SELECT 
                date,
                currentTimestamp,
                category,
                'PerHundred' AS entryType,
                perHundredGram * 1.0,
                perHundredQuantity * 1.0,
                gramTotal * 1.0
            FROM entry_table
            WHERE isPerHundred = 1
        """.trimIndent()
                )

                // Move "isPerHundred == 0" rows → entryPerPiece_table
                // Assign entryType = 'PerPiece'
                database.execSQL(
                    """
            INSERT INTO entryPerPiece_table (
                date,
                currentTimestamp,
                category,
                entryType,
                perPieceGram,
                perPieceAmount,
                gramTotal
            )
            SELECT 
                date,
                currentTimestamp,
                category,
                'PerPiece' AS entryType,
                perPieceGram * 1.0,
                perPieceAmount * 1.0,
                gramTotal * 1.0
            FROM entry_table
            WHERE isPerHundred = 0
        """.trimIndent()
                )

                // Drop the old table now that data has been migrated
                database.execSQL("DROP TABLE entry_table")

                // Drop the calories table since this feature is discontinued
                database.execSQL("DROP TABLE entry_calories")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(application: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        application.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).addMigrations(migration_10_11)
                        .fallbackToDestructiveMigration(false)
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
