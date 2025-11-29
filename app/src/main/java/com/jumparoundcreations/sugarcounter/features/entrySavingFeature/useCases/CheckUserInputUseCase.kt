package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.CheckUserInputResult

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