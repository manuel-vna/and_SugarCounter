package com.jumparoundcreations.mva_sugarcounter.ui.components.counterUI

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.util.NumberConstants
import com.jumparoundcreations.mva_sugarcounter.util.TimeConstants

@Composable
fun RowScope.DatePicker(
    onAction: (EntrySavingIntents) -> Unit,
    entrySavingStates: EntrySavingStates,
    textColor: Color,
) {
    val nowMillis = System.currentTimeMillis()
    val xDaysAgoMillis = nowMillis - TimeConstants.MONTH_ONE_IN_MILLISECONDS

    val datePickerState =
        rememberDatePickerState(
            initialDisplayMode = DisplayMode.Picker,
            selectableDates =
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                        utcTimeMillis in (xDaysAgoMillis + NumberConstants.ONE_AS_INT)..<nowMillis
                },
        )

    ElevatedButton(
        onAction = onAction,
        accessibilityDatePickerButton = stringResource(R.string.accessibility_date_picker_button),
        textColor = textColor,
        dateOfEntryEpochSec = entrySavingStates.dateOfEntryEpochSec,
    )

    if (entrySavingStates.datePickerShown) {
        DatePickerDialog(
            onAction = onAction,
            datePickerState = datePickerState,
        )
    }
}

@Composable
fun RowScope.ElevatedButton(
    onAction: (EntrySavingIntents) -> Unit,
    accessibilityDatePickerButton: String,
    textColor: Color,
    dateOfEntryEpochSec: Long,
) {
    ElevatedButton(
        modifier =
            Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
                .semantics {
                    contentDescription = accessibilityDatePickerButton
                },
        onClick = {
            onAction(EntrySavingIntents.OpenAndCloseDatePicker)
        },
        colors =
            elevatedButtonColors(
                contentColor = textColor,
            ),
        border = BorderStroke(width = 1.dp, color = textColor),
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Rounded.CalendarMonth,
            contentDescription = "",
        )
        Text(
            text =
                HelperMethods.convertTimestampToDateString(
                    timestamp = dateOfEntryEpochSec,
                    format = TimeConstants.DATE_SHORT_DAY,
                ),
        )
    }
}

@Composable
fun DatePickerDialog(
    onAction: (EntrySavingIntents) -> Unit,
    datePickerState: DatePickerState,
) {
    DatePickerDialog(
        onDismissRequest = {
            onAction(EntrySavingIntents.OpenAndCloseDatePicker)
        },
        confirmButton = {
            Button(onClick = {
                onAction(EntrySavingIntents.OpenAndCloseDatePicker)
                datePickerState.selectedDateMillis?.let {
                    onAction(
                        EntrySavingIntents.ChangeSelectedDate(
                            epochTime = it / TimeConstants.MILLISECONDS_TO_SECONDS_DIVIDER,
                        ),
                    )
                }
            }) {
                Text(text = stringResource(id = R.string.saveButton))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onAction(EntrySavingIntents.OpenAndCloseDatePicker)
                },
            ) {
                Text(text = stringResource(id = R.string.generalCancel))
            }
        },
    ) {
        androidx.compose.material3.DatePicker(
            title = {
                Text(
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp,
                    text = stringResource(R.string.entryDateDescription),
                )
            },
            headline = {
                Text(
                    modifier = Modifier.padding(2.dp),
                    fontSize = 18.sp,
                    text = stringResource(R.string.entryDateTitle),
                )
            },
            state = datePickerState,
        )
    }
}
