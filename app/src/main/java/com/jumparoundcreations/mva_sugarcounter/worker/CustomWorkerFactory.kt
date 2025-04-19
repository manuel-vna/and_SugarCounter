package com.jumparoundcreations.mva_sugarcounter.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase

class CustomWorkerFactory(
    private val dao: DaoAppDatabase,
    private val sharedPrefsMain: SharedPreferences
) : WorkerFactory() {

    override fun createWorker(
        context: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            DeletionWorker::class.java.name ->
                DeletionWorker(context, workerParameters, dao, sharedPrefsMain)

            else ->
                null
        }
    }
}