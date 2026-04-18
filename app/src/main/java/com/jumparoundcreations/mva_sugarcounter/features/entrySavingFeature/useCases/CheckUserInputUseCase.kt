package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckUserInputResult

class CheckUserInputUseCase {

    operator fun invoke(state: EntrySavingStates): CheckUserInputResult {
        return if (state.categoryInField.isEmpty()) {
            CheckUserInputResult.NoCategoryGiven
        } else if (
            state.entryFieldGram.isEmpty() ||
            state.entryFieldQuantity.isEmpty()
        ) {
            CheckUserInputResult.NoGramDataGivenButCategoryGiven

        } else {
            CheckUserInputResult.InputDataComplete
        }
    }
}