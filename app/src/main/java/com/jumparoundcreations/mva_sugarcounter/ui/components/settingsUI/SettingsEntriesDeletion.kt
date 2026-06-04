package com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.BottomSheetsSettings
import com.jumparoundcreations.mva_sugarcounter.features.settingsFeature.SettingsVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsEntriesDeletion(settingsVM: SettingsVM) {
    val settingsStates by settingsVM.settingsStates.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val deletionWorkerSliderValueRange = 0f..8f
    val deletionWorkerSliderValueRangeMap =
        mapOf(
            0 to stringResource(R.string.deletion_period_half_year),
            1 to stringResource(R.string.deletion_period_one_year),
            2 to stringResource(R.string.deletion_period_two_years),
            3 to stringResource(R.string.deletion_period_three_years),
            4 to stringResource(R.string.deletion_period_four_years),
            5 to stringResource(R.string.deletion_period_five_years),
            6 to stringResource(R.string.deletion_period_six_years),
            7 to stringResource(R.string.deletion_period_seven_years),
            8 to stringResource(R.string.deletion_period_eight_years),
        )

    if (settingsStates.bottomSheetsSettings == BottomSheetsSettings.ENTRIES_DELETION) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.NONE)
            },
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                        .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Text(
                        text = stringResource(R.string.settings_entries_deletion_button_title),
                        fontWeight = FontWeight.Bold,
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                settingsVM.actionChangeEntriesDeletionActivated(
                                    settingsStates.entriesDeletionActivated.not(),
                                )
                            }.padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = stringResource(id = R.string.settings_entries_deletion_bottom_sheet_description_switch))

                    Switch(
                        checked = settingsStates.entriesDeletionActivated,
                        onCheckedChange = { settingsVM.actionChangeEntriesDeletionActivated(it) },
                    )
                }

                if (settingsStates.entriesDeletionActivated) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        val gram = stringResource(R.string.general_gram)
                        Slider(
                            modifier =
                                Modifier.semantics {
                                    contentDescription = "${settingsStates.deletionWorkerSlider} $gram"
                                },
                            value = settingsStates.deletionWorkerSlider.toFloat(),
                            onValueChange = { settingsVM.actionUpdateDeletionWorkerSlider(it) },
                            valueRange = deletionWorkerSliderValueRange,
                            steps = 7,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text(
                            modifier = Modifier.padding(end = 12.dp),
                            text = deletionWorkerSliderValueRangeMap[settingsStates.deletionWorkerSlider] ?: "0",
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 16.dp),
                            text =
                                stringResource(
                                    id = R.string.settings_entries_deletion_description,
                                    deletionWorkerSliderValueRangeMap[settingsStates.deletionWorkerSlider].toString(),
                                ),
                        )
                    }
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    OutlinedButton(
                        onClick = { settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.NONE) },
                    ) {
                        Text(
                            text = stringResource(R.string.generalClose),
                        )
                    }
                }
            }
        }
    }
}
