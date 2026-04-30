package com.jumparoundcreations.mva_sugarcounter.ui.components.counterUI

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckThresholdResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.UserThresholdBreachReaction
import com.jumparoundcreations.mva_sugarcounter.ui.components.ShowAlertDialogDoubleBtn
import com.jumparoundcreations.mva_sugarcounter.ui.components.ShowAlertDialogSingleBtn
import com.jumparoundcreations.mva_sugarcounter.ui.events.ScanUiEvents
import kotlinx.coroutines.flow.Flow

@Composable
fun CounterUserInformation(
    context: Context,
    entrySavingStates: EntrySavingStates,
    onAction: (EntrySavingIntents) -> Unit,
    scanUiEvents: Flow<ScanUiEvents>,
    snackbarHostState: SnackbarHostState,
) {

    if (entrySavingStates.savingProcessMissingSugarData) {
        ShowAlertDialogSingleBtn(
            dialogTitle = stringResource(id = R.string.foodType),
            dialogDescription = stringResource(id = R.string.categoryInputOnlyDescription),
            confirmBtnText = stringResource(id = R.string.generalClose),
            confirmBtnAction = {
                onAction(EntrySavingIntents.DismissNoSugarDataEnteredAlert)
            },
            onDismissRequest = {
                onAction(EntrySavingIntents.DismissNoSugarDataEnteredAlert)
            },
        )
    }

    if (entrySavingStates.savingProcessMissingCategoryData) {
        ShowAlertDialogSingleBtn(
            dialogTitle = stringResource(id = R.string.foodType),
            dialogDescription = "No data entered",
            confirmBtnText = stringResource(id = R.string.generalClose),
            confirmBtnAction = {
                onAction(EntrySavingIntents.DismissNoCategoryDataEnteredAlert)
            },
            onDismissRequest = {
                onAction(EntrySavingIntents.DismissNoCategoryDataEnteredAlert)
            },
        )
    }

    if (entrySavingStates.savingProcessDailyGramThreshold is
                CheckThresholdResult.DailyThresholdBreached
    ) {
        ShowAlertDialogDoubleBtn(
            dialogTitle = stringResource(id = R.string.alertSugarThresholdTitle),
            dialogDescription = stringResource(id = R.string.alertSugarThresholdDescription),
            confirmBtnText = stringResource(id = R.string.general_delete),
            confirmBtnAction = {
                onAction(
                    EntrySavingIntents.UserThresholdReaction(
                        userThresholdBreachReaction = UserThresholdBreachReaction.DeleteLastEnteredEntry
                    )
                )
            },
            dismissBtnText = stringResource(id = R.string.alertThresholdDismissBtn),
            dismissBtnAction = {
                onAction(
                    EntrySavingIntents.UserThresholdReaction(
                        userThresholdBreachReaction = UserThresholdBreachReaction.KeepLastEnteredEntry
                    )
                )
            },
            onDismissRequest = {
                onAction(
                    EntrySavingIntents.UserThresholdReaction(
                        userThresholdBreachReaction = UserThresholdBreachReaction.KeepLastEnteredEntry
                    )
                )
            }
        )
    }

    LaunchedEffect(snackbarHostState) {
        scanUiEvents.collect { event ->
            when (event) {
                is ScanUiEvents.ScanResultNoCategoryForBarcode -> {
                    snackbarHostState.showSnackbar(
                        message = "ToDo: ScanResultNoCategoryForBarcode",
                        duration = SnackbarDuration.Short
                    )
                }

                is ScanUiEvents.ScanResultFailed -> {
                    snackbarHostState.showSnackbar(
                        message = "ToDo: Scan failed",
                        duration = SnackbarDuration.Short
                    )
                }

                is ScanUiEvents.CategoryEditNoDataForChosenCategory -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.no_entry_found_for_given_category_Snackbar_text),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

}
