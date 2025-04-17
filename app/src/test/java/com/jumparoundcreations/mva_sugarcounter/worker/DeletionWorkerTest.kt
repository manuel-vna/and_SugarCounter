package com.jumparoundcreations.mva_sugarcounter.worker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.database.DaoAppDatabase
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods.Companion.convertTimestampToDateString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertTrue


@ExperimentalCoroutinesApi
class DeletionWorkerTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: DaoAppDatabase
    private lateinit var context: Context

    @BeforeEach
    fun setup() {

        context = ApplicationProvider.getApplicationContext()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.appDao()

        // Fill database with test data
        val timestampInSeconds = 123
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

    }

    @AfterEach
    fun tearDown() {
        db.close()
    }

    @Test
    fun testWorkerDeletesExpectedRows() = runTest {

        val allSugarEntries = dao.getAllEntries()

        assertTrue(allSugarEntries.isNotEmpty(), "Expected non-empty list")


        /*
        // Instantiate your worker with a test context and db access
        val worker = DeletionWorker(
            appContext = context,
            workerParams = TestWorkerBuilder<MyWorker>(context, this).build(),
            myDao = dao // If your worker uses DI, inject the test dao
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)

        val remainingRows = dao.getAll()
        assertEquals(7, remainingRows.size)

        */

    }
}
