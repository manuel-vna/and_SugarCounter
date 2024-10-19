package com.jumparoundcreations.mva_sugarcounter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jumparoundcreations.mva_sugarcounter.data.Category
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.util.CustomTypeConverter

@Database(entities = [Entry::class, EntryCalories::class, Category::class], version = 8)
@TypeConverters(CustomTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {


    abstract fun appDao(): DaoAppDatabase

    companion object {
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
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
