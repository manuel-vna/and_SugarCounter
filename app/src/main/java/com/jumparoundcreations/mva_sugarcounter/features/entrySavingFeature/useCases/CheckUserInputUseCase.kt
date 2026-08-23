package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckUserInputResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode

class CheckUserInputUseCase {

    operator fun invoke(state: EntrySavingStates): CheckUserInputResult = when {
        state.categoryInField.isEmpty() ->
            CheckUserInputResult.NoCategoryGiven

        state.isGramDataMissing() ->
            CheckUserInputResult.NoGramDataGivenButCategoryGiven

        else ->
            CheckUserInputResult.InputDataComplete
    }

    private fun EntrySavingStates.isGramDataMissing(): Boolean = when (gramCountMode) {
        GramCountMode.PerHundred ->
            entryFieldGramPerHundred.isEmpty() || entryFieldQuantity.isEmpty()

        GramCountMode.PerPiece ->
            entryFieldGramPerPiece.isEmpty()
    }

}
