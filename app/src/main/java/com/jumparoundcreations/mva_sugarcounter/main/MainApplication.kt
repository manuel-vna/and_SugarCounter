package com.jumparoundcreations.mva_sugarcounter.main

import android.app.Application
import android.content.SharedPreferences
import androidx.work.Configuration
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.di.appModule
import com.jumparoundcreations.mva_sugarcounter.worker.CustomWorkerFactory
import com.jumparoundcreations.mva_sugarcounter.worker.DeletionWorker
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication :
    Application(),
    Configuration.Provider {
    lateinit var workerFactory: CustomWorkerFactory

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }

        // WorkFactory for DeletionWorker
        val db = AppDatabase.getInstance(this)
        val dao = db.appDao()
        val sharedPrefs = getKoin().get<SharedPreferences>()
        workerFactory = CustomWorkerFactory(dao, sharedPrefs)
        DeletionWorker.scheduleDeletionWorker(this)
    }

    override val workManagerConfiguration: Configuration
        get() {
            return Configuration
                .Builder()
                .setMinimumLoggingLevel(android.util.Log.VERBOSE)
                .setWorkerFactory(workerFactory)
                .build()
        }
}
