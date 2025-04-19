package com.jumparoundcreations.mva_sugarcounter.main

import android.app.Application
import androidx.work.Configuration
import com.jumparoundcreations.mva_sugarcounter.di.appModule
import com.jumparoundcreations.mva_sugarcounter.worker.CustomWorkerFactory
import com.jumparoundcreations.mva_sugarcounter.worker.DeletionWorker
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication() : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }

        DeletionWorker.scheduleDeletionWorker(this)
    }

    override val workManagerConfiguration: Configuration
        get() {
            return Configuration.Builder()
                .setWorkerFactory(getKoin().get<CustomWorkerFactory>())
                .build()
        }

}