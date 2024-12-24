package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import com.jumparoundcreations.mva_sugarcounter.worker.WorkerDeletionPeriods

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsButtonEntriesDeletion(
    context: Context,
    settingsVM: SettingsVM,
    descriptionText: String,
    buttonIcon: Int,
) {

    val entriesDeletionPeriodSelected by settingsVM.entriesDeletionPeriodSelected.collectAsState()
    val entriesDeletionBottomSheetShown by settingsVM._entriesDeletionBottomSheetShown.collectAsState()
    val entriesDeletionActivated by settingsVM._entriesDeletionActivated.collectAsState()


    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp),
        onClick = {
            settingsVM.actionChangeEntriesDeletionBottomSheetShown(true)
        }) {
        Text(text = "$descriptionText   ")
        Icon(
            painter = painterResource(id = buttonIcon),
            contentDescription = "",
        )
    }

    if (entriesDeletionBottomSheetShown) {

        ModalBottomSheet(
            onDismissRequest = {
                settingsVM.actionChangeEntriesDeletionBottomSheetShown(false)
            }
        ) {

            Column(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.settings_entires_deletion_bottom_sheet_description_switch))

                    Switch(
                        checked = entriesDeletionActivated,
                        onCheckedChange = { settingsVM.actionChangeEntriesDeletionActivated(it) }
                    )
                }


                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    ),
                    text = stringResource(id = R.string.settings_entires_deletion_bottom_sheet_description_period_choice_top)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val buttonOptions = listOf(
                        WorkerDeletionPeriods.AfterHalfAYear,
                        WorkerDeletionPeriods.AfterOneYear,
                        WorkerDeletionPeriods.AfterTwoYears,
                        WorkerDeletionPeriods.AfterFiveYears
                    )
                    var selectedIndex by remember { mutableIntStateOf(0) }

                    SingleChoiceSegmentedButtonRow {
                        buttonOptions.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = selectedIndex == index,
                                onClick = {
                                    selectedIndex = index
                                    settingsVM.actionChangeEntriesDeletionPeriodSelected(option)
                                },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = 4)
                            )
                            {
                                Text(
                                    text = option.deletionPeriodUI
                                )
                            }

                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(id = R.string.settings_entires_deletion_bottom_sheet_description_period_choice_bottom)
                    )
                }

            }

        }

    }


}