package com.jumparoundcreations.sugarcounter.composables.counterUI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.elevatedButtonColors
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingViewModel
import com.jumparoundcreations.sugarcounter.util.GeneralConstants
import com.jumparoundcreations.sugarcounter.util.HelperMethods
import com.jumparoundcreations.sugarcounter.util.TimeConstants


@Composable
fun RowScope.DatePicker(
    entrySavingViewModel: EntrySavingViewModel,
    textColor: Color
) {
    val nowMillis = System.currentTimeMillis()
    val xDaysAgoMillis = nowMillis - TimeConstants.MONTH_ONE_IN_MILLISECONDS
    val entrySavingStates by entrySavingViewModel.entrySavingStates.collectAsStateWithLifecycle()

    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in (xDaysAgoMillis + GeneralConstants.ONE_AS_INT)..<nowMillis
            }
        }
    )

    ElevatedButton(
        entrySavingViewModel = entrySavingViewModel,
        accessibilityDatePickerButton = stringResource(R.string.accessibility_date_picker_button),
        textColor = textColor,
        dateOfEntryEpochSec = entrySavingStates.dateOfEntryEpochSec
    )

    if (entrySavingStates.datePickerShown) {
        DatePickerDialog(
            entrySavingViewModel = entrySavingViewModel,
            datePickerState = datePickerState
        )
    }
}

@Composable
fun RowScope.ElevatedButton(
    entrySavingViewModel: EntrySavingViewModel,
    accessibilityDatePickerButton: String,
    textColor: Color,
    dateOfEntryEpochSec: Long
) {
    ElevatedButton(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .weight(1f)
            .semantics {
                contentDescription = accessibilityDatePickerButton
            },
        onClick = {
            entrySavingViewModel.onAction(EntrySavingIntents.OpenAndCloseDatePicker)
        },
        colors = elevatedButtonColors(
            contentColor = textColor
        ),
        border = BorderStroke(width = 1.dp, color = textColor)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Rounded.CalendarMonth,
            contentDescription = "",
        )
        Text(
            text = HelperMethods.convertTimestampToDateString(
                timestamp = dateOfEntryEpochSec,
                format = TimeConstants.DATE_SHORT_DAY
            )
        )
    }
}

@Composable
fun DatePickerDialog(
    entrySavingViewModel: EntrySavingViewModel,
    datePickerState: DatePickerState
) {

    DatePickerDialog(
        onDismissRequest = {
            entrySavingViewModel.onAction(EntrySavingIntents.OpenAndCloseDatePicker)
        },
        confirmButton = {
            Button(onClick = {
                entrySavingViewModel.onAction(EntrySavingIntents.OpenAndCloseDatePicker)
                datePickerState.selectedDateMillis?.let {
                    entrySavingViewModel.onAction(
                        action = EntrySavingIntents.ChangeSelectedDate(
                            epochTime = it / TimeConstants.MILLISECONDS_TO_SECONDS_DIVIDER
                        )
                    )
                }
            }) {
                Text(text = stringResource(id = R.string.saveButton))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    entrySavingViewModel.onAction(EntrySavingIntents.OpenAndCloseDatePicker)
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