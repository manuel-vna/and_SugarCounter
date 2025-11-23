package com.jumparoundcreations.sugarcounter.composables.counterUI

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.composables.ShowAlertDialogDoubleBtn
import com.jumparoundcreations.sugarcounter.composables.ShowAlertDialogSingleBtn
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingViewModel
import com.jumparoundcreations.sugarcounter.util.HelperMethods
import com.jumparoundcreations.sugarcounter.viewModels.CounterVM
import org.koin.compose.getKoin
import org.koin.compose.koinInject


@Composable
fun Counter(
    context: Context,
    snackbarHostState: SnackbarHostState,
    sharedPrefsMain: SharedPreferences = koinInject()
) {
    val counterVM = getKoin().get<CounterVM>()
    val entrySavingViewModel = getKoin().get<EntrySavingViewModel>()

    // States

    val category by counterVM.categorySelected.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }
    // SharedPreferences: Calories Counter: Activation state retrieved from sharedPrefsMain
    val caloriesCounterActivated = sharedPrefsMain.getBoolean(
        "caloriesCounterActivated",
        false
    )
    val noBarcodeYetInfo by counterVM.noBarcodeYetInfoTitle.collectAsState()
    val barcodeNumber by counterVM.barcodeNumber.collectAsState()

    //Keyboard
    val keyboardController = LocalSoftwareKeyboardController.current

    // Style
    val darkMode = HelperMethods.checkForUIMode(context)
    val textColor = if (darkMode == 33) Color.White else Color.Black


    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    counterVM.actionChangeCategoryFieldExpanded(false)
                }
            )
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            Arrangement.Absolute.SpaceAround
        ) {
            DatePicker(
                entrySavingViewModel = entrySavingViewModel,
                textColor = textColor,
            )

            Barcode(
                entrySavingViewModel = entrySavingViewModel,
                textColor = textColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (noBarcodeYetInfo) {
                NoBarcodeYetInfo(counterVM, barcodeNumber)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp)
        ) {

            CategoryDropdownField(
                context = context,
                counterVM = counterVM,
                caloriesCounterActivated = caloriesCounterActivated,
                keyboardController = keyboardController
            )
        }

        TabRow(entrySavingViewModel)

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier
                    .width(160.dp),
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
                    keyboardController?.hide()
                },
            ) {
                Text(
                    text = stringResource(id = R.string.saveButton),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

        }

        CounterCardsAreaUI(
            counterVM,
        )

    }


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