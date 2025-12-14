package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.sugarcounter.data.categoryData.Category
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.database.DaoAppDatabase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DisplayAllCategoriesUseCaseTest {

    private lateinit var mockDatabase: AppDatabase
    private lateinit var mockDao: DaoAppDatabase
    private lateinit var useCase: DisplayAllCategoriesUseCase

    @Before
    fun setUp() {
        mockDatabase = mockk()
        mockDao = mockk()
        coEvery { mockDatabase.appDao() } returns mockDao
        useCase = DisplayAllCategoriesUseCase(mockDatabase)
    }

    @Test
    fun `invoke returns the flow of categories from the database DAO`() = runTest {
        // Arrange
        val expectedCategories = listOf(
            Category(id = 1, category = "Soda", barcodeNumber = "111"),
            Category(id = 2, category = "Candy", barcodeNumber = "222")
        )

        // 2. Create a fake Flow that will emit our fake list
        val expectedFlow = flowOf(expectedCategories)

        // 3. Configure the mock DAO to return our fake Flow when observeAllCategories() is called
        coEvery { mockDao.observeAllCategories() } returns expectedFlow

        // Act
        // 4. Call the use case to get the actual Flow
        val actualFlow = useCase()

        // Assert
        // 5. Assert that the Flow returned by the use case is the same instance as our fake Flow
        assertEquals(expectedFlow, actualFlow)

        // 6. Collect the first item from the flow to confirm the data is correct
        val firstEmission = actualFlow.first()
        assertEquals(expectedCategories, firstEmission)
        assertEquals("Soda", firstEmission[0].category)
    }

    @Test
    fun `invoke returns an empty flow when the database has no categories`() = runTest {
        // Arrange
        // Create a Flow that emits an empty list
        val emptyFlow = flowOf(emptyList<Category>())

        // Configure the mock DAO to return the empty flow
        coEvery { mockDao.observeAllCategories() } returns emptyFlow

        // Act
        val actualFlow = useCase()

        // Assert
        assertEquals(emptyFlow, actualFlow)

        val firstEmission = actualFlow.first()
        assertEquals(0, firstEmission.size)
    }
}