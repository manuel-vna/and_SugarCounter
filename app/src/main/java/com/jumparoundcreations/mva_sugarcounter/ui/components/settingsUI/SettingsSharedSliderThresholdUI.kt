package com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.jumparoundcreations.mva_sugarcounter.ui.components.ShowAlertDialogDoubleBtn
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM


@Composable
fun SettingsSharedSliderThreshold(
    settingsVM: SettingsVM
) {
    val gramThresholdSlider by settingsVM.gramThresholdSlider.collectAsState()

    val title = stringResource(id = R.string.settings_threshold_title)
    val titleSugar = stringResource(id = R.string.general_sugar)
    val gramSliderValueRange = 0f..100f
    val unitOfMeasureGram = stringResource(id = R.string.general_gram)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold
        )
    }
    HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))

    Column(
        modifier = Modifier.padding(top = 8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = titleSugar)
            Text(
                modifier = Modifier.padding(end = 6.dp),
                text = "${gramThresholdSlider.toInt()} $unitOfMeasureGram"
            )
        }

        val gram = stringResource(R.string.general_gram)
        Slider(
            modifier = Modifier.semantics {
                contentDescription = "${gramThresholdSlider.toInt()} ${gram}"
            },
            value = gramThresholdSlider,
            onValueChange = { settingsVM.actionUpdateGramThresholdSlider(it) },
            valueRange = gramSliderValueRange
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                modifier = Modifier.padding(start = 6.dp),
                onClick = { settingsVM.actionGramThresholdDialogCheck(true) }
            ) {
                Text(text = stringResource(id = R.string.saveButton))
            }
        }
    }

    val gramThresholdDialogCheck by settingsVM.gramThresholdDialogCheck.collectAsState()
    if (gramThresholdDialogCheck) {

        ShowAlertDialogDoubleBtn(
            dialogTitle = gramThresholdSlider.toInt()
                .toString() + " " + stringResource(R.string.general_gram),
            dialogDescription = stringResource(id = R.string.settings_threshold_dialog_title),
            confirmBtnText = stringResource(id = R.string.generalConfirm),
            confirmBtnAction = {
                settingsVM.actionGramThresholdDialogCheck(false)
                settingsVM.actionUpdateGramThresholdSharedPref()
            },
            dismissBtnText = stringResource(id = R.string.generalCancel),
            dismissBtnAction = { settingsVM.actionGramThresholdDialogCheck(false) },
            onDismissRequest = { settingsVM.actionGramThresholdDialogCheck(false) }
        )
    }
}