package com.jumparoundcreations.mva_sugarcounter.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.concurrent.TimeUnit

class EntryDeletionWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val sharedPrefsMain by inject<SharedPreferences>(qualifier = named("sharedPrefsMain"))
    private val appDatabase = AppDatabase.getInstance(context)

    //private val deletionPeriod = 94670778 // = three years in seconds
    private val deletionPeriod = 604800 // = 7 days

    companion object {
        //private const val WORK_REPEAT_INTERVAL_IN_DAYS = 30L
        private const val WORK_REPEAT_INTERVAL_IN_DAYS = 1L // 1L == 1 day as Long
        fun scheduleEntryDeletionWorker(context: Context) {

            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest =
                PeriodicWorkRequestBuilder<EntryDeletionWorker>(
                    WORK_REPEAT_INTERVAL_IN_DAYS,
                    TimeUnit.DAYS
                )
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "entryDeletionWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }

    override suspend fun doWork(): Result {

        val entriesDeletionActivated = sharedPrefsMain.getBoolean("entriesDeletionActivated", false)
        if (entriesDeletionActivated) {
            val deletionPointInTime =
                (System.currentTimeMillis() / 1000) - deletionPeriod
            appDatabase.appDao().deleteEntriesSugarOlderThanN(deletionPointInTime)
            appDatabase.appDao().deleteEntriesCaloriesOlderThanN(deletionPointInTime)

            return Result.success()
        } else {
            return Result.failure()
        }

    }

}