package com.jumparoundcreations.mva_sugarcounter.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.util.TimeConstants
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

class DeletionWorker(
    context: Context,
    params: WorkerParameters,
    private val dao: DaoAppDatabase,
    private val sharedPrefsMain: SharedPreferences,
) : CoroutineWorker(context, params),
    KoinComponent {
    val automaticDeletionValue = sharedPrefsMain.getInt("automaticDeletionValue", YEAR_THREE)
    private val deletionPeriod =
        automaticDeletionValueMapping[automaticDeletionValue] ?: YEAR_THREE_IN_SECONDS

    companion object {
        private const val YEAR_HALF = 0
        private const val YEAR_ONE = 1
        private const val YEAR_TWO = 2
        private const val YEAR_THREE = 3
        private const val YEAR_FOUR = 4
        private const val YEAR_FIVE = 5
        private const val YEAR_SIX = 6
        private const val YEAR_SEVEN = 7
        private const val YEAR_EIGHT = 8

        private const val YEAR_HALF_IN_SECONDS = 15778463
        private const val YEAR_ONE_IN_SECONDS = 31556926
        private const val YEAR_TWO_IN_SECONDS = 63113852
        private const val YEAR_THREE_IN_SECONDS = 94670778
        private const val YEAR_FOUR_IN_SECONDS = 126227704
        private const val YEAR_FIVE_IN_SECONDS = 157784630
        private const val YEAR_SIX_IN_SECONDS = 189341556
        private const val YEAR_SEVEN_IN_SECONDS = 220898482
        private const val YEAR_EIGHT_IN_SECONDS = 252455408

        val automaticDeletionValueMapping =
            mapOf(
                YEAR_HALF to YEAR_HALF_IN_SECONDS, // 1/2 year
                YEAR_ONE to YEAR_ONE_IN_SECONDS, // 1 year
                YEAR_TWO to YEAR_TWO_IN_SECONDS, // 2 years
                YEAR_THREE to YEAR_THREE_IN_SECONDS, // 3 years
                YEAR_FOUR to YEAR_FOUR_IN_SECONDS, // 4 years
                YEAR_FIVE to YEAR_FIVE_IN_SECONDS, // 5 years
                YEAR_SIX to YEAR_SIX_IN_SECONDS, // 6 years
                YEAR_SEVEN to YEAR_SEVEN_IN_SECONDS, // 7 years
                YEAR_EIGHT to YEAR_EIGHT_IN_SECONDS, // 8 years
            )
        private const val WORK_REPEAT_INTERVAL_IN_DAYS = 30L

        fun scheduleDeletionWorker(context: Context) {
            val constraints =
                Constraints
                    .Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()

            val workRequest =
                PeriodicWorkRequestBuilder<DeletionWorker>(
                    WORK_REPEAT_INTERVAL_IN_DAYS,
                    TimeUnit.DAYS,
                ).setConstraints(constraints)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "entryDeletionWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest,
            )
        }
    }

    override suspend fun doWork(): Result {
        val entriesDeletionActivated = sharedPrefsMain.getBoolean("entriesDeletionActivated", false)
        if (entriesDeletionActivated) {
            val deletionPointInTime =
                (System.currentTimeMillis() / TimeConstants.MILLISECONDS_TO_SECONDS_DIVIDER) - deletionPeriod

            // <editor-fold desc="SugarEntries">
            val categoriesOfSugarEntriesToBeDeleted =
                dao.getCategoriesOfSugarEntriesToBeDeleted(deletionPointInTime)
            val categoryExistsInMoreThanOneEntryRow =
                categoriesOfSugarEntriesToBeDeleted.map {
                    Pair(
                        first = it,
                        second = dao.checkIfCategoryIsPresentSinceInSugarTable(it, deletionPointInTime),
                    )
                }
            val categoriesToBeDeletedFromSugarEntries =
                categoryExistsInMoreThanOneEntryRow
                    .filter {
                        it.second.not()
                    }.map { it.first }
            // </editor-fold>

            // <editor-fold desc="Deletion of Categories">
            categoriesToBeDeletedFromSugarEntries.forEach {
                dao.deleteSpecificCategoryByName(it)
            }
            // </editor-fold>

            // <editor-fold desc="Deletion of Sugar Entries">
            dao.deleteEntriesSugarOlderThanN(deletionPointInTime)
            // </editor-fold>

            return Result.success()
        } else {
            return Result.failure()
        }
    }
}
