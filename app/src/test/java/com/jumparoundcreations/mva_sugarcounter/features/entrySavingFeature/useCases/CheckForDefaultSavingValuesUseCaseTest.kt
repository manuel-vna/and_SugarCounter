package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CheckForDefaultSavingValuesUseCaseTest {
    private lateinit var useCase: CheckForDefaultSavingValuesUseCase

    @Before
    fun setUp() {
        // Instantiate the use case before each test
        useCase = CheckForDefaultSavingValuesUseCase()
    }

    @Test
    fun `invoke returns true when amount is empty and mode is PerPiece`() {
        // --- Arrange ---
        // The state that matches the required conditions for the use case to return true
        val state =
            EntrySavingStates(
                entryFieldAmount = "", // Condition 1: amount is empty
                gramCountMode = GramCountMode.PerPiece, // Condition 2: mode is PerPiece
            )

        // --- Act ---
        val result = useCase(state)

        // --- Assert ---
        // The result should be true because both conditions are met
        assertTrue("Should return true when amount is empty and mode is PerPiece", result)
    }

    @Test
    fun `invoke returns false when amount is not empty and mode is PerPiece`() {
        // --- Arrange ---
        // Create a state where the amount field has a value
        val state =
            EntrySavingStates(
                entryFieldAmount = "1", // This fails the first condition
                gramCountMode = GramCountMode.PerPiece,
            )

        // --- Act ---
        val result = useCase(state)

        // --- Assert ---
        // The result should be false because the amount is not empty
        assertFalse("Should return false when amount is not empty", result)
    }

    @Test
    fun `invoke returns false when amount is empty and mode is not PerPiece`() {
        // --- Arrange ---
        // Create a state where the gramCountMode is not PerPiece
        val state =
            EntrySavingStates(
                entryFieldAmount = "",
                gramCountMode = GramCountMode.PerHundred,
            )

        // --- Act ---
        val result = useCase(state)

        // --- Assert ---
        // The result should be false because the gramCountMode is not PerPiece
        assertFalse("Should return false when mode is not PerPiece", result)
    }

    @Test
    fun `invoke returns false when amount is not empty and mode is not PerPiece`() {
        // --- Arrange ---
        // Create a state where neither of the conditions is met
        val state =
            EntrySavingStates(
                entryFieldAmount = "2",
                gramCountMode = GramCountMode.PerHundred,
            )

        // --- Act ---
        val result = useCase(state)

        // --- Assert ---
        // The result should be false because neither condition is met
        assertFalse("Should return false when both conditions fail", result)
    }
}
