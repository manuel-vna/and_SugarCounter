package com.jumparoundcreations.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import java.util.concurrent.TimeUnit


class CategoryDeletionWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appDatabase = AppDatabase.getInstance(context)

    companion object {
        fun scheduleCategoryDeletionWorker(context: Context) {
            val workRequest =
                PeriodicWorkRequestBuilder<CategoryDeletionWorker>(20, TimeUnit.MINUTES)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "categoryDeletionWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }

    override suspend fun doWork(): Result {
        val categoryTableRowCount = appDatabase.appDao().getCategoryTableRowCount()
        if (categoryTableRowCount > 15) {
            val oldestEntries = appDatabase.appDao().getOldestUniqueEntries()
            oldestEntries.forEach {
                appDatabase.appDao().deleteSpecificEntryRow(it.id)
            }
        }
        return Result.success()
    }

}

