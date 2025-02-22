package com.jumparoundcreations.mva_sugarcounter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jumparoundcreations.mva_sugarcounter.data.Category
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.util.CustomTypeConverter

@Database(entities = [Entry::class, EntryCalories::class, Category::class], version = 9)
@TypeConverters(CustomTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): DaoAppDatabase

    companion object {

        private val migration_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER calories_table ADD COLUMN 'caloriesPerPiece' INTEGER")
                db.execSQL("ALTER calories_table ADD COLUMN 'caloriesAmount' INTEGER")
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
                    ).addMigrations(migration_8_9)
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
