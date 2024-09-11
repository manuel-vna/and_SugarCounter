package com.jumparoundcreations.mva_sugarcounter

import android.app.Application
import com.jumparoundcreations.mva_sugarcounter.di.appModule
import com.jumparoundcreations.mva_sugarcounter.worker.CategoryDeletionWorker
import com.jumparoundcreations.mva_sugarcounter.worker.EntryDeletionWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }

        CategoryDeletionWorker.scheduleCategoryDeletionWorker(this)
        EntryDeletionWorker.scheduleEntryDeletionWorker(this)

    }
}