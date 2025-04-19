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
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

class DeletionWorker(
    context: Context,
    params: WorkerParameters,
    private val dao: DaoAppDatabase,
    private val sharedPrefsMain: SharedPreferences
) : CoroutineWorker(context, params), KoinComponent {

    val automaticDeletionValue = sharedPrefsMain.getInt("automaticDeletionValue", 3)
    val automaticDeletionValueMapping = mapOf(
        0 to 86400, // 86400 = 1 day # // 15778463, // 1/2 year
        1 to 172800, // 1728000 = 2 days # // 31556926, // 1 year
        2 to 259200, // 259200 = 3 days # // 63113852, // 2 years
        3 to 94670778, // 3 years
        4 to 126227704, // 4 years
        5 to 157784630, // 5 years
        6 to 189341556, // 6 years
        7 to 220898482, // 7 years
        8 to 252455408, // 8 years
    )
    private val deletionPeriod = automaticDeletionValueMapping[automaticDeletionValue] ?: 94670778


    companion object {
        private const val WORK_REPEAT_INTERVAL_IN_DAYS = 20L
        fun scheduleDeletionWorker(context: Context) {

            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest =
                PeriodicWorkRequestBuilder<DeletionWorker>(
                    WORK_REPEAT_INTERVAL_IN_DAYS,
                    //TimeUnit.DAYS
                    TimeUnit.MINUTES
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

            // 86400 = 1 day in seconds
            val deletionPointInTime = (System.currentTimeMillis() / 1000) - deletionPeriod

            //<editor-fold desc="SugarEntries">
            val categoriesOfSugarEntriesToBeDeleted =
                dao.getCategoriesOfSugarEntriesToBeDeleted(deletionPointInTime)
            val categoryExistsInMoreThanOneEntryRow = categoriesOfSugarEntriesToBeDeleted.map {
                Pair(
                    first = it,
                    second = dao.checkIfCategoryIsPresentSinceInSugarTable(it, deletionPointInTime)
                )
            }
            val categoriesToBeDeletedFromSugarEntries = categoryExistsInMoreThanOneEntryRow.filter {
                it.second.not()
            }.map { it.first }
            //</editor-fold>

            //<editor-fold desc="CaloriesEntries">
            val categoriesOfCaloriesEntriesToBeDeleted =
                dao.getCategoriesOfCaloriesEntriesToBeDeleted(deletionPointInTime)

            val categoryExistsInMoreThanOneCaloriesRow =
                categoriesOfCaloriesEntriesToBeDeleted.map {
                    Pair(
                        first = it,
                        second = dao
                            .checkIfCategoryIsPresentSinceInCaloriesTable(it, deletionPointInTime)
                    )
                }

            val categoriesToBeDeletedFromCaloriesEntries =
                categoryExistsInMoreThanOneCaloriesRow.filter {
                    it.second.not()
                }.map { it.first }
            //</editor-fold>

            //<editor-fold desc="Deletion of Categories">
            val categoriesToBeDeletedOverall =
                categoriesToBeDeletedFromSugarEntries + categoriesToBeDeletedFromCaloriesEntries

            categoriesToBeDeletedOverall.forEach {
                dao.deleteSpecificCategoryByName(it)
            }
            //</editor-fold>

            //<editor-fold desc="Deletion of Sugar Entries">
            dao.deleteEntriesSugarOlderThanN(deletionPointInTime)
            //</editor-fold>

            //<editor-fold desc="Deletion of Calories Entries">
            dao.deleteEntriesCaloriesOlderThanN(deletionPointInTime)
            //</editor-fold>


            return Result.success()
        } else {
            return Result.failure()
        }

    }


}