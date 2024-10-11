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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.jumparoundcreations.mva_sugarcounter.composables.ShowSugarCountItemsShared
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun Counter(
    context: Context,
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
            .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
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
                context, counterVM, caloriesCounterActivated,
                modifier = Modifier.weight(2f)
            )

            CounterCaloriesUI(
                caloriesCounterActivated = caloriesCounterActivated,
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
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
                    fontSize = 16.sp,
                )
            }

        }

        val savedSugarCountGrouped by counterVM.savedEntriesToday.collectAsState()

        LazyColumn {
            items(savedSugarCountGrouped) {
                ShowSugarCountItemsShared(
                    entryGroup = it,
                    backgroundColorPrimary = false
                )
            }
        }
    }

    val alertDialog by counterVM.alertDialog.collectAsState()
    if (alertDialog) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.noGramValueInDatabaseYetTitle)) },
            onDismissRequest = { },
            confirmButton = {
                Button(

                    onClick = {
                        counterVM.actionDismissAlertDialog()
                    }) {
                    Text("Okay")
                }
            },
            text = {
                Text(stringResource(id = R.string.noGramValueInDatabaseYetDescription))
            }
        )
    }

    val alertDialogGramThreshold by counterVM.alertDialogGramThreshold.collectAsState()
    if (alertDialogGramThreshold) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.alertGramThresholdTitle)) },
            onDismissRequest = { },
            confirmButton = {
                Button(
                    onClick = {
                        counterVM.actionGramThresholdDeleteLastEntry()
                    }) {
                    Text(stringResource(id = R.string.alertGramThresholdConfirmBtn))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        counterVM.actionGramThresholdKeepLastEntry()
                    }) {
                    Text(stringResource(id = R.string.alertGramThresholdDismissBtn))
                }
            },
            text = {
                Text(stringResource(id = R.string.alertGramThresholdDescription))
            }
        )
    }
}