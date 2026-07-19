package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode

class CheckForDefaultSavingValuesUseCase {
    operator fun invoke(state: EntrySavingStates): Boolean =
        state.entryFieldAmount.isEmpty() &&
            state.gramCountMode == GramCountMode.PerPiece
}
