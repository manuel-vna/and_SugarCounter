package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature

import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.UserThresholdBreachReaction

sealed interface EntrySavingIntents {
    data object OpenAndCloseDatePicker : EntrySavingIntents

    data class ChangeSelectedDate(
        val epochTime: Long,
    ) : EntrySavingIntents

    data object ScanBarcode : EntrySavingIntents

    data object ClearBarcodeData : EntrySavingIntents

    data object ChangeBarcodeInfoSheetShown : EntrySavingIntents

    data class EditOfCategoryField(
        val categoryInField: String,
        val categoryDropdownExpanded: Boolean,
    ) : EntrySavingIntents

    data class ExpandOrCollapseCategoryDropdown(
        val categoryDropdownExpanded: Boolean,
    ) : EntrySavingIntents

    data class EditOfCategoryWithinDropdown(
        val categoryInDropdown: String,
        val categoryDropdownExpanded: Boolean,
    ) : EntrySavingIntents

    data class ChangeGramCountMode(
        val gramCountMode: GramCountMode,
    ) : EntrySavingIntents

    data class ChangeGramCountModeTabIndex(
        val tabIndex: Int,
    ) : EntrySavingIntents

    data class ChangeEntryFieldGram(
        val entryFieldGram: String,
    ) : EntrySavingIntents

    data class ChangeEntryFieldQuantity(
        val entryFieldQuantity: String,
    ) : EntrySavingIntents

    data object SaveSugarEntry : EntrySavingIntents

    data object ClearInputFields : EntrySavingIntents

    data object DismissNoCategoryDataEnteredAlert : EntrySavingIntents

    data object DismissNoSugarDataEnteredAlert : EntrySavingIntents

    data class UserThresholdReaction(
        val userThresholdBreachReaction: UserThresholdBreachReaction,
    ) : EntrySavingIntents
}
