package com.jumparoundcreations.mva_sugarcounter.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import java.util.concurrent.TimeUnit

class EntryDeletionWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val appDatabase = AppDatabase.getInstance(context)
    private val MAXIMUM_AMOUNT_ENTRIES = 9999
    private val AMOUNT_OF_ENTRIES_TO_DELETE = 100

    companion object {
        private const val WORK_REPEAT_INTERVAL_IN_DAYS = 30L
        fun scheduleEntryDeletionWorker(context: Context) {
            val workRequest =
                PeriodicWorkRequestBuilder<EntryDeletionWorker>(
                    WORK_REPEAT_INTERVAL_IN_DAYS,
                    TimeUnit.DAYS
                )
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "entryDeletionWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }

    override suspend fun doWork(): Result {
        val entryTableRowCount = appDatabase.appDao().getEntryTableRowCount()
        if (entryTableRowCount > MAXIMUM_AMOUNT_ENTRIES) {
            appDatabase.appDao().deleteOldestNEntries(AMOUNT_OF_ENTRIES_TO_DELETE)
        }
        return Result.success()
    }

}