package com.jumparoundcreations.mva_sugarcounter.composables

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.data.IEntry
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun <T : IEntry> ShowSharedCards(
    entryGroup: EntryGroup<T>,
    backgroundColorPrimary: Boolean,
    sharedPrefsMain: SharedPreferences = koinInject(qualifier = named("sharedPrefsMain"))
) {

    val counterVM: CounterVM = koinViewModel()
    val totalGramPerDayBlock = HelperMethods.calculateTotalGramPerDayBlock(entryGroup.entryList)
    var thresholdValue = 0

    if (entryGroup.entryList.isNotEmpty()) {
        thresholdValue = when (entryGroup.entryList.first()) {
            is Entry -> sharedPrefsMain.getInt("gramThresholdValue", 50)
            is EntryCalories -> sharedPrefsMain.getInt("caloriesThresholdValue", 2250)
            else -> 0
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp),
        border = if (totalGramPerDayBlock > thresholdValue) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.error)
        } else {
            BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        },
        colors = CardDefaults.cardColors(
            containerColor = if (backgroundColorPrimary) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface,
        )
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp),
        ) {

            Text(
                modifier = Modifier.padding(top = 6.dp), text = when (entryGroup.dayDisplayFormat) {
                    "TODAY" -> stringResource(R.string.timestampToday)
                    "YESTERDAY" -> stringResource(
                        R.string.timestampYesterday
                    )

                    else -> entryGroup.dayDisplayFormat
                }, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontStyle = FontStyle.Italic
            )

        }

        entryGroup.entryList.forEach {

            var firstColumnText = ""
            val secondColumnText = it.category
            var thirdColumnText = ""

            when (it) {
                is Entry -> {
                    firstColumnText = if (it.isPerHundred) {
                        it.perHundredQuantity.toString() +
                                stringResource(id = R.string.sugar_card_relation_expression) +
                                it.perHundredGram
                    } else {
                        it.perPieceAmount.toString() +
                                stringResource(id = R.string.sugar_card_multiplier_expression) +
                                it.perPieceGram.toString()
                    }
                    thirdColumnText =
                        it.gramTotal.toString() + stringResource(id = R.string.gram_unit_short)
                }

                is EntryCalories -> {
                    firstColumnText = stringResource(id = R.string.calories_unit_short)
                    thirdColumnText = it.caloriesTotal.toString()
                }

                else -> {
                    firstColumnText = ""
                    thirdColumnText = ""
                }

            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        counterVM.actionShowDeleteAlertDialog(it)
                    },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier.weight(3f), contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        textAlign = TextAlign.Start,
                        text = firstColumnText,

                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                Box(
                    modifier = Modifier.weight(8f), contentAlignment = Alignment.Center
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = secondColumnText,
                        modifier = Modifier.padding(horizontal = 2.dp),
                    )
                }
                Box(
                    modifier = Modifier.weight(2f), contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        textAlign = TextAlign.End,
                        text = thirdColumnText,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .padding(top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                painter = if (totalGramPerDayBlock <= thresholdValue) painterResource(id = R.drawable.baseline_check_circle_outline_24) else painterResource(
                    id = R.drawable.baseline_remove_circle_outline_24
                ),
                tint = if (totalGramPerDayBlock > thresholdValue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                contentDescription = "",
            )

            Text(
                modifier = Modifier.padding(end = 8.dp),
                text =
                stringResource(id = R.string.totalAmountSugar) + ": " + HelperMethods.calculateTotalGramPerDayBlock(
                    entryGroup.entryList
                ),
                fontWeight = FontWeight.Bold
            )
        }

    }

    val showDeleteDialog by counterVM.showDeleteDialog.collectAsState()
    val itemToDeleteEntrySugar by counterVM.itemToDeleteEntrySugar.collectAsState()
    val itemToDeleteEntryCalories by counterVM.itemToDeleteEntryCalories.collectAsState()
    val itemToDeleteIsEntrySugar by counterVM.itemToDeleteIsEntrySugar.collectAsState()

    if (showDeleteDialog) {

        ShowAlertDialogDoubleBtn(
            dialogTitle = if (itemToDeleteIsEntrySugar) itemToDeleteEntrySugar.category else itemToDeleteEntryCalories.category,
            dialogDescription = stringResource(id = R.string.general_delete_question),
            confirmBtnText = stringResource(id = R.string.general_delete),
            confirmBtnAction = {
                val itemToDelete =
                    if (itemToDeleteIsEntrySugar) itemToDeleteEntrySugar else itemToDeleteEntryCalories
                counterVM.actionDeleteSpecificEntryRow(itemToDelete.id)
                counterVM.actionDismissDeleteAlertDialog()
            },
            dismissBtnText = stringResource(R.string.generalCancel),
            dismissBtnAction = {
                counterVM.actionDismissDeleteAlertDialog()
            },
            onDismissRequest = { counterVM.actionDismissDeleteAlertDialog() }
        )

    }
}











