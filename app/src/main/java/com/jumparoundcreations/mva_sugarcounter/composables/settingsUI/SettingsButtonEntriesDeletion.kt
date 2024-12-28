package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsButtonEntriesDeletion(
    settingsVM: SettingsVM,
    descriptionText: String,
    buttonIcon: Int,
) {

    val entriesDeletionBottomSheetShown by settingsVM.entriesDeletionBottomSheetShown.collectAsState()
    val entriesDeletionActivated by settingsVM.entriesDeletionActivated.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


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
            sheetState = sheetState,
            onDismissRequest = {
                settingsVM.actionChangeEntriesDeletionBottomSheetShown(false)
            }
        ) {

            Column(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 32.dp)
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.settings_entries_deletion_bottom_sheet_description_switch))

                    Switch(
                        checked = entriesDeletionActivated,
                        onCheckedChange = { settingsVM.actionChangeEntriesDeletionActivated(it) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(id = R.string.settings_entries_deletion_bottom_sheet_description_top)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(id = R.string.settings_entries_deletion_bottom_sheet_description_bottom)
                    )
                }

            }

        }

    }


}