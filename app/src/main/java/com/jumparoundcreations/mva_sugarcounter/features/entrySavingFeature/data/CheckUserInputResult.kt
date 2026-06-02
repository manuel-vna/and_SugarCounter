package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data

sealed class CheckUserInputResult {
    data object NoCategoryGiven : CheckUserInputResult()

    data object NoGramDataGivenButCategoryGiven : CheckUserInputResult()

    data object InputDataComplete : CheckUserInputResult()
}
