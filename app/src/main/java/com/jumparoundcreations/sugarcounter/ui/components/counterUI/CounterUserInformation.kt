package com.jumparoundcreations.sugarcounter.ui.components.counterUI

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingViewModel
import com.jumparoundcreations.sugarcounter.ui.components.ShowAlertDialogDoubleBtn
import com.jumparoundcreations.sugarcounter.ui.components.ShowAlertDialogSingleBtn
import com.jumparoundcreations.sugarcounter.ui.events.ScanUiEvents

@Composable
fun CounterUserInformation(
    context: Context,
    entrySavingViewModel: EntrySavingViewModel,
    snackbarHostState: SnackbarHostState,
) {

    val entrySavingStates by entrySavingViewModel.entrySavingStates.collectAsState()

    //val alertDialog by counterVM.alertDialog.collectAsState()
    //if (alertDialog) {
    if (false) {

        ShowAlertDialogSingleBtn(
            dialogTitle = stringResource(id = R.string.foodType),
            dialogDescription = stringResource(id = R.string.categoryInputOnlyDescription),
            confirmBtnText = stringResource(id = R.string.generalClose),
            confirmBtnAction = { }, //counterVM.actionDismissAlertDialog() },
            onDismissRequest = { }, //counterVM.actionDismissAlertDialog() }
        )

    }

    //val alertDialogGramThreshold by counterVM.alertSugarThreshold.collectAsState()
    //if (alertDialogGramThreshold) {
    if (false) {
        ShowAlertDialogDoubleBtn(
            dialogTitle = stringResource(id = R.string.alertSugarThresholdTitle),
            dialogDescription = stringResource(id = R.string.alertSugarThresholdDescription),
            confirmBtnText = stringResource(id = R.string.general_delete),
            confirmBtnAction = { }, //counterVM.actionGramThresholdDeleteLastEntry() },
            dismissBtnText = stringResource(id = R.string.alertThresholdDismissBtn),
            dismissBtnAction = { }, // counterVM.actionGramThresholdKeepLastEntry() },
            onDismissRequest = { }
        )
    }

    LaunchedEffect(snackbarHostState) {
        entrySavingViewModel.scanUiEvents.collect { event ->
            when (event) {
                is ScanUiEvents.ScanResultNoCategoryForBarcode -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.no_entry_found_for_given_category_Snackbar_text),
                        duration = SnackbarDuration.Short
                    )
                }

                is ScanUiEvents.ScanResultFailed -> {
                    snackbarHostState.showSnackbar(
                        message = "Scan failed",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }


}
