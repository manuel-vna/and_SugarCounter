package com.jumparoundcreations.mva_sugarcounter.composables.counterUI

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.ShowAlertDialogDoubleBtn
import com.jumparoundcreations.mva_sugarcounter.composables.ShowAlertDialogSingleBtn
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named


@Composable
fun Counter(
    context: Context,
    snackbarHostState: SnackbarHostState,
    sharedPrefsMain: SharedPreferences = koinInject(qualifier = named("sharedPrefsMain"))
) {
    val counterVM: CounterVM = koinViewModel()

    // States
    val category by counterVM.categorySelected.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }
    // SharedPreferences: Calories Counter: Activation state retrieved from sharedPrefsMain
    val caloriesCounterActivated = sharedPrefsMain.getBoolean(
        "caloriesCounterActivated",
        false
    )
    //Keyboard
    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        modifier = Modifier
            .padding(start = 30.dp, end = 30.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    counterVM.actionChangeCategoryFieldExpanded(false)
                }
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 6.dp),
            Arrangement.Absolute.SpaceAround
        ) {
            DatePicker(counterVM = counterVM)

            Barcode(counterVM)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 16.dp)
        ) {

            CategoryDropdownField(
                context = context,
                counterVM = counterVM,
                caloriesCounterActivated = caloriesCounterActivated,
                keyboardController = keyboardController
            )

            CounterCaloriesUI(
                caloriesCounterActivated = caloriesCounterActivated
            )

        }

        TabRow(counterVM)

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier.width(160.dp),
                onClick = {
                    counterVM.saveEntry(category)
                    counterVM.categoryHandling(category)
                    // clearing fields by an empty value / setting fields back to default
                    counterVM.actionChangeSelectedCategory("")
                    counterVM.actionPerPieceGramChange("")
                    counterVM.actionPerPieceAmountChange("")
                    counterVM.actionPerHundredChange("")
                    counterVM.actionPerHundredQuantityChange("")
                    counterVM.actionChangeCategoryFieldExpanded(false)
                    counterVM.actionCaloriesChange("")
                    keyboardController?.hide()
                },
            ) {
                Text(
                    text = stringResource(id = R.string.saveButton),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }

        }

        CounterCardsAreaUI(
            counterVM,
            caloriesCounterActivated
        )

    }

    val alertDialog by counterVM.alertDialog.collectAsState()
    if (alertDialog) {

        ShowAlertDialogSingleBtn(
            dialogTitle = stringResource(id = R.string.categoryInputOnlyTitle),
            dialogDescription = stringResource(id = R.string.categoryInputOnlyDescription),
            confirmBtnText = stringResource(id = R.string.generalClose),
            confirmBtnAction = { counterVM.actionDismissAlertDialog() },
            onDismissRequest = { counterVM.actionDismissAlertDialog() }
        )

    }

    val alertDialogGramThreshold by counterVM.alertDialogGramThreshold.collectAsState()
    if (alertDialogGramThreshold) {
        ShowAlertDialogDoubleBtn(
            dialogTitle = stringResource(id = R.string.alertSugarThresholdTitle),
            dialogDescription = stringResource(id = R.string.alertSugarThresholdDescription),
            confirmBtnText = stringResource(id = R.string.alertThresholdConfirmBtn),
            confirmBtnAction = { counterVM.actionGramThresholdDeleteLastEntry() },
            dismissBtnText = stringResource(id = R.string.alertThresholdDismissBtn),
            dismissBtnAction = { counterVM.actionGramThresholdKeepLastEntry() },
            onDismissRequest = { }
        )
    }

    val alertCaloriesThreshold by counterVM.alertCaloriesThreshold.collectAsState()
    if (alertCaloriesThreshold) {
        ShowAlertDialogDoubleBtn(
            dialogTitle = stringResource(id = R.string.alertCaloriesThresholdTitle),
            dialogDescription = stringResource(id = R.string.alertCaloriesThresholdDescription),
            confirmBtnText = stringResource(id = R.string.alertThresholdConfirmBtn),
            confirmBtnAction = { counterVM.actionCaloriesThresholdDeleteLastEntry() },
            dismissBtnText = stringResource(id = R.string.alertThresholdDismissBtn),
            dismissBtnAction = { counterVM.actionCaloriesThresholdKeepLastEntry() },
            onDismissRequest = { counterVM.actionCaloriesThresholdKeepLastEntry() }
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