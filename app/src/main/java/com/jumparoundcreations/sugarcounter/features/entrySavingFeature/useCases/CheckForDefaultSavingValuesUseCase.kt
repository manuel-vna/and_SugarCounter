package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode

class CheckForDefaultSavingValuesUseCase {

    operator fun invoke(state: EntrySavingStates): Boolean {
        return state.entryFieldQuantity.isEmpty() &&
                state.gramCountMode == GramCountMode.PerPiece
    }
}