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
    private val MAXIMUM_AMOUNT_CATEGORIES = 100
    private val AMOUNT_OF_ENTRIES_TO_DELETE = 5

    companion object {
        private const val WORK_REPEAT_INTERVAL_IN_DAYS = 30L
        fun scheduleCategoryDeletionWorker(context: Context) {
            val workRequest =
                PeriodicWorkRequestBuilder<CategoryDeletionWorker>(
                    WORK_REPEAT_INTERVAL_IN_DAYS,
                    TimeUnit.DAYS
                )
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
        if (categoryTableRowCount > MAXIMUM_AMOUNT_CATEGORIES) {
            val oldestEntries = appDatabase.appDao()
                .getOldestUniqueEntries(amountOfEntries = AMOUNT_OF_ENTRIES_TO_DELETE)
            oldestEntries.forEach {
                appDatabase.appDao().deleteSpecificCategoryByName(it.category)
            }
        }
        return Result.success()
    }

}

