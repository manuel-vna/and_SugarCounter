package com.jumparoundcreations.mva_sugarcounter.composables.counterUI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.elevatedButtonColors
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.DatePicker(
    counterVM: CounterVM,
    textColor: Color
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

    ElevatedButton(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .weight(1f),
        onClick = {
            counterVM.actionChangeDatePickerVisibility(!datePickerShown)
        },
        colors = elevatedButtonColors(
            contentColor = textColor
        ),
        border = BorderStroke(width = 1.dp, color = textColor)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Rounded.CalendarMonth,
            contentDescription = "date",
        )
        Text(
            text = HelperMethods.convertTimestampToDateString(
                dateOfEntryEpochSec,
                " EE dd.MM."
            )
        )
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