package com.jumparoundcreations.mva_sugarcounter.composables.counterUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    counterVM: CounterVM,
) {
    val nowMillis = System.currentTimeMillis()
    val xDaysAgoMillis = nowMillis - 2629743000

    val dateOfEntryEpochSec by counterVM.dateOfEntryEpochSec.collectAsState()
    val datePickerShown by counterVM.datePickerShown.collectAsState()
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in (xDaysAgoMillis + 1)..<nowMillis
            }
        }
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                counterVM.actionChangeDatePickerVisibility(!datePickerShown)
            }) {
            Text(HelperMethods.formatDateToString(dateOfEntryEpochSec, "EEEE dd.MM.yy"))
        }
    }

    if (datePickerShown) {
        DatePickerDialog(
            onDismissRequest = {
                counterVM.actionChangeDatePickerVisibility(false)
            },
            confirmButton = {
                Button(onClick = {
                    counterVM.actionChangeDatePickerVisibility(false)
                    datePickerState.selectedDateMillis?.let {
                        counterVM.actionChangeDateOfEntryM3(
                            it / 1000
                        )
                    }
                }) {
                    Text(text = stringResource(id = R.string.saveButton))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        counterVM.actionChangeDatePickerVisibility(false)
                    }
                ) {
                    Text(text = stringResource(id = R.string.generalCancel))
                }
            }) {
            androidx.compose.material3.DatePicker(
                title = {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp,
                        text = stringResource(R.string.entryDateDescription)
                    )
                },
                headline = {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        fontSize = 18.sp,
                        text = stringResource(R.string.entryDateTitle)
                    )
                },
                state = datePickerState,
            )
        }
    }
}