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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.BottomSheetsSettings
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsColorScheme(settingsVM: SettingsVM) {

    val settingsStates by settingsVM.settingsStates.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (settingsStates.bottomSheetsSettings == BottomSheetsSettings.COLOR_SCHEME) {

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.NONE)
            }
        ) {

            Column(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = stringResource(R.string.settings_color_scheme_title),
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingsVM.actionChangeDynamicColorActivated(
                                settingsStates.dynamicColorActivated.not()
                            )
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.settings_use_dynamic_colors_from_os))

                    Switch(
                        checked = settingsStates.dynamicColorActivated,
                        onCheckedChange = { settingsVM.actionChangeDynamicColorActivated(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(id = R.string.settings_use_dynamic_colors_from_os_description)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.NONE) }
                    ) {
                        Text(
                            text = stringResource(R.string.generalClose)
                        )
                    }
                }

            }

        }

    }


}