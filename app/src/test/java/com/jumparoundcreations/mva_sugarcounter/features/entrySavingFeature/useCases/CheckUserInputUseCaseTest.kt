package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckUserInputResult
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CheckUserInputUseCaseTest {
    private lateinit var useCase: CheckUserInputUseCase

    @Before
    fun setUp() {
        useCase = CheckUserInputUseCase()
    }

    @Test
    fun `invoke returns NoCategoryGiven when category field is empty`() {
        // Arrange
        val state =
            EntrySavingStates(
                categoryInField = "",
                entryFieldGram = "10",
                entryFieldQuantity = "1",
            )

        // Act
        val result = useCase(state)

        // Assert
        assertEquals(CheckUserInputResult.NoCategoryGiven, result)
    }

    @Test
    fun `invoke returns NoGramDataGiven when gram field is empty`() {
        // Arrange
        val state =
            EntrySavingStates(
                categoryInField = "Cereal",
                entryFieldGram = "", // The failing condition
                entryFieldQuantity = "1",
            )

        // Act
        val result = useCase(state)

        // Assert
        assertEquals(CheckUserInputResult.NoGramDataGivenButCategoryGiven, result)
    }

    @Test
    fun `invoke returns NoGramDataGiven when quantity field is empty`() {
        // Arrange
        val state =
            EntrySavingStates(
                categoryInField = "Cereal",
                entryFieldGram = "45",
                entryFieldQuantity = "", // The failing condition
            )

        // Act
        val result = useCase(state)

        // Assert
        assertEquals(CheckUserInputResult.NoGramDataGivenButCategoryGiven, result)
    }

    @Test
    fun `invoke returns InputDataComplete when all fields are filled`() {
        // Arrange
        val state =
            EntrySavingStates(
                categoryInField = "Cereal",
                entryFieldGram = "45",
                entryFieldQuantity = "2",
            )

        // Act
        val result = useCase(state)

        // Assert
        assertEquals(CheckUserInputResult.InputDataComplete, result)
    }
}
