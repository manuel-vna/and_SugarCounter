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
import com.jumparoundcreations.sugarcounter.ui.components.ShowAlertDialogDoubleBtn
import com.jumparoundcreations.sugarcounter.ui.components.ShowAlertDialogSingleBtn

@Composable
fun CounterUserInformation(
    context: Context,
    snackbarHostState: SnackbarHostState,
){

    val alertDialog by counterVM.alertDialog.collectAsState()
    if (alertDialog) {

        ShowAlertDialogSingleBtn(
            dialogTitle = stringResource(id = R.string.foodType),
            dialogDescription = stringResource(id = R.string.categoryInputOnlyDescription),
            confirmBtnText = stringResource(id = R.string.generalClose),
            confirmBtnAction = { counterVM.actionDismissAlertDialog() },
            onDismissRequest = { counterVM.actionDismissAlertDialog() }
        )

    }

    val alertDialogGramThreshold by counterVM.alertSugarThreshold.collectAsState()
    if (alertDialogGramThreshold) {
        ShowAlertDialogDoubleBtn(
            dialogTitle = stringResource(id = R.string.alertSugarThresholdTitle),
            dialogDescription = stringResource(id = R.string.alertSugarThresholdDescription),
            confirmBtnText = stringResource(id = R.string.general_delete),
            confirmBtnAction = { counterVM.actionGramThresholdDeleteLastEntry() },
            dismissBtnText = stringResource(id = R.string.alertThresholdDismissBtn),
            dismissBtnAction = { counterVM.actionGramThresholdKeepLastEntry() },
            onDismissRequest = { }
        )
    }

    val noDataForChosenCategorySnackbarShown by counterVM.noDataForChosenCategorySnackbarShown.collectAsState()
    if (noDataForChosenCategorySnackbarShown) {
        // Launch a coroutine to show the Snackbar
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.no_entry_found_for_given_category_Snackbar_text),
                duration = SnackbarDuration.Short
            )
            counterVM.actionNoDataForChosenCategorySnackbarShownChange(false)
        }
    }

}