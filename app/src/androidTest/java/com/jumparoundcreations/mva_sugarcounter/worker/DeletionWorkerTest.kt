package com.jumparoundcreations.mva_sugarcounter.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods.Companion.convertTimestampToDateString
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import kotlin.random.Random


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DeletionWorkerTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: DaoAppDatabase
    private lateinit var context: Context
    private lateinit var mockPrefs: SharedPreferences

    private var sevenMonthsAgo = 0L


    @Before
    fun setup() {

        context = ApplicationProvider.getApplicationContext()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.appDao()

        // Mock SharedPreferences
        mockPrefs = mockk()
        every { mockPrefs.getInt("automaticDeletionValue", any()) } returns 0
        every { mockPrefs.getBoolean("entriesDeletionActivated", any()) } returns true

        val oneYearAgo =
            (System.currentTimeMillis() / 1000) - 31556926 // 31556926 = 1 year in seconds
        val halfAYearAgo =
            (System.currentTimeMillis() / 1000) - 15778463L // 15778463 = 1/2 year in seconds
        sevenMonthsAgo =
            (System.currentTimeMillis() / 1000) - 18408201L // 18408201 = 7 months in seconds



        // Fill database with test data
        val timestampInSeconds = oneYearAgo
        val yearsTimespan = 1
        val sugarTestData = true
        val caloriesTestData = true

        runBlocking {
            var timestamp = timestampInSeconds.toLong()
            repeat((365 * yearsTimespan)) {
                timestamp += 86400

                repeat((1..4).random()) {
                    val gramValue = Random.nextInt(from = 1, until = 20)
                    val caloriesValue = Random.nextInt(from = 200, until = 1200)

                    if (sugarTestData) {
                        dao.insertEntry(
                            Entry(
                                currentTimestamp = timestamp,
                                date = convertTimestampToDateString(
                                    timestamp,
                                    "yyyy-MM-dd"
                                ),
                                category = "TestSugar",
                                isPerHundred = true,
                                perPieceGram = gramValue,
                                perPieceAmount = 1,
                                perHundredGram = 0,
                                perHundredQuantity = 0,
                                gramTotal = gramValue
                            )
                        )
                    }

                    if (caloriesTestData) {
                        dao.insertEntryCalories(
                            EntryCalories(
                                currentTimestamp = timestamp,
                                date = convertTimestampToDateString(
                                    timestamp,
                                    "yyyy-MM-dd"
                                ),
                                category = "TestCalories",
                                caloriesPerPiece = 120,
                                caloriesAmount = 2,
                                caloriesTotal = caloriesValue
                            )
                        )
                    }
                }

            }
        }

        // Initialize WorkManager for instrumentation tests
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(CustomWorkerFactory(dao, mockPrefs))
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

    }


    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testWorkerDeletesExpectedRows() {

        val allSugarEntries = dao.getAllEntries()
        val numberBeforeDeletion = allSugarEntries.size
        assertTrue(allSugarEntries.isNotEmpty())

        val request = OneTimeWorkRequestBuilder<DeletionWorker>().build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(request).result.get()

        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        assertNotNull(testDriver)
        testDriver?.setAllConstraintsMet(request.id)
        testDriver?.setInitialDelayMet(request.id)

        val workerState = awaitSuccessOrFail(workManager, request.id)
        println("Worker state: $workerState")
        val workInfo = workManager.getWorkInfoById(request.id).get()
        println("workInfo: $workInfo ")
        assertEquals(WorkInfo.State.SUCCEEDED, workInfo?.state)

        // Check that the number of sugar entries is smaller after the DeletionWorker was running
        val numberAfterDeletion = dao.getAllEntries().size
        println("numberOfSugarEntriesBeforeDeletion: $numberBeforeDeletion")
        println("numberOfSugarEntriesAfterDeletion: $numberAfterDeletion")
        assertTrue(numberBeforeDeletion > numberAfterDeletion)

        // Check that the last sugar entry is not older than 6 months
        // by checking if its timestamp is bigger than the timestamp of seven month ago
        val allLeftEntries = dao.getAllEntries()
        val timestampOfEntry = allLeftEntries.first().currentTimestamp
        assertTrue(timestampOfEntry > sevenMonthsAgo)


    }

    fun awaitSuccessOrFail(
        workManager: WorkManager,
        requestId: UUID,
        timeoutMs: Long = 5000
    ): WorkInfo.State {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            val state = workManager.getWorkInfoById(requestId).get()?.state
            if (state == WorkInfo.State.SUCCEEDED || state == WorkInfo.State.FAILED) {
                return state
            }
            Thread.sleep(100)
        }
        throw AssertionError("Worker did not finish within timeout")
    }

}