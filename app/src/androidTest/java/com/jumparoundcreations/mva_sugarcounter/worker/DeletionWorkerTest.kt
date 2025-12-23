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
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
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
import kotlin.properties.Delegates
import kotlin.random.Random


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DeletionWorkerTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: DaoAppDatabase
    private lateinit var context: Context
    private lateinit var mockPrefs: SharedPreferences
    private var numberOfSugarEntriesBeforeDeletion by Delegates.notNull<Int>()
    private lateinit var workInfo: WorkInfo

    private var threeMonthsAgo = 0L
    private var sixMonthsAgo = 0L


    @Before
    fun setup() {

        context = ApplicationProvider.getApplicationContext()

        //<editor-fold desc="Setup Room test database">
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.appDao()
        //</editor-fold>

        //<editor-fold desc="Mock Shared Preferences">
        mockPrefs = mockk()
        every { mockPrefs.getInt("automaticDeletionValue", any()) } returns 0
        every { mockPrefs.getBoolean("entriesDeletionActivated", any()) } returns true
        //</editor-fold>

        //<editor-fold desc="Set timestamps used for testing">
        val oneYearAgo =
            (System.currentTimeMillis() / 1000) - 31556926 // 31556926 = 1 year in seconds
        threeMonthsAgo =
            (System.currentTimeMillis() / 1000) - 7889229L // 7889229 = 3 months in seconds
        sixMonthsAgo =
            (System.currentTimeMillis() / 1000) - 15778458L // 15778458 = 6 months in seconds
        //</editor-fold>


        //<editor-fold desc="Fill test database with test data">
        val timestampInSeconds = oneYearAgo
        val yearsTimespan = 1
        val sugarTestData = true

        runBlocking {
            var timestamp = timestampInSeconds.toLong()
            repeat((365 * yearsTimespan)) {
                timestamp += 86400

                repeat((1..4).random()) {
                    val gramValue = Random.nextInt(from = 1, until = 20)

                    if (sugarTestData) {
                        dao.insertSugarEntry(
                            SugarEntry(
                                currentTimestamp = timestamp,
                                date = convertTimestampToDateString(
                                    timestamp,
                                    "yyyy-MM-dd"
                                ),
                                category = "Test Sugar",
                                entryType = GramCountMode.PerPiece,
                                gram = 7.5,
                                quantity = 2.0,
                                gramTotal = 15.0
                            )
                        )
                    }
                }

            }
        }
        //</editor-fold>

        //<editor-fold desc="Initialize WorkManager for instrumentation tests">
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(CustomWorkerFactory(dao, mockPrefs))
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        val sugarEntriesBeforeDeletion = dao.getAllEntries()
        numberOfSugarEntriesBeforeDeletion = sugarEntriesBeforeDeletion.size
        //</editor-fold>

        //<editor-fold desc="Run DeletionWorker">
        val request = OneTimeWorkRequestBuilder<DeletionWorker>().build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(request).result.get()

        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        assertNotNull(testDriver)
        testDriver?.setAllConstraintsMet(request.id)
        testDriver?.setInitialDelayMet(request.id)
        //</editor-fold>

        //<editor-fold desc="Get result of DeletionWorker run">
        val workerState = awaitSuccessOrFail(workManager, request.id)
        println("Worker state: $workerState")
        workInfo = workManager.getWorkInfoById(request.id).get()!!
        println("workInfo: $workInfo ")
        //</editor-fold>

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

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testThatDeletionWorkerRanSuccessfully() {

        // Test
        assertEquals(WorkInfo.State.SUCCEEDED, workInfo.state)
    }

    @Test
    fun testThatNumberOfSugarEntriesIsSmallerAfterWorkerWasRunning() {

        // Prepare
        val numberOfSugarEntriesAfterDeletion = dao.getAllEntries().size
        println("numberOfSugarEntriesBeforeDeletion: $numberOfSugarEntriesBeforeDeletion")
        println("numberOfSugarEntriesAfterDeletion: $numberOfSugarEntriesAfterDeletion")

        // Test
        assertTrue(numberOfSugarEntriesBeforeDeletion > numberOfSugarEntriesAfterDeletion)

    }

    @Test
    fun checkThatLatestSugarEntryIsWithin6MonthsRange() {

        // Prepare
        val allLeftEntries = dao.getAllEntries()
        val timestampOfEntry = allLeftEntries.first().currentTimestamp

        println("timestampOfEntry: $timestampOfEntry")
        println("sixMonthsAgo: $sixMonthsAgo")

        // Test
        assertTrue(timestampOfEntry > sixMonthsAgo)
    }

    @Test
    fun checkThatLatestSugarEntryIsOlderThan3MonthsAgo() {

        // Prepare
        val allLeftEntries = dao.getAllEntries()
        val timestampOfEntry = allLeftEntries.first().currentTimestamp

        // Test
        assertTrue(timestampOfEntry < threeMonthsAgo)
    }


}