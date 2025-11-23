package com.jumparoundcreations.sugarcounter.ui.components.settingsUI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.viewModels.CounterVM
import com.jumparoundcreations.sugarcounter.viewModels.HistoryVM
import com.jumparoundcreations.sugarcounter.viewModels.SettingsVM
import org.koin.compose.getKoin



@Composable
fun SettingsActivateCaloriesCounter(
    settingsVM: SettingsVM
) {

    val caloriesCounterActivated by settingsVM.caloriesCounterActivated.collectAsState()
    val historyVM = getKoin().get<HistoryVM>()
    val counterVM = getKoin().get<CounterVM>()


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(id = R.string.calories_activation_switch_title),
            fontWeight = FontWeight.Bold
        )
    }
    HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { settingsVM.actionChangeCaloriesCounterGeneral(caloriesCounterActivated.not()) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.calories_activation_switch_description)
        )
        Switch(
            checked = caloriesCounterActivated,
            onCheckedChange = {
                settingsVM.actionChangeCaloriesCounterGeneral(it)
                // HistoryUI: set the segmentedButton to the selection 'Sugar'
                historyVM.actionChangeSegmentedButtonSugarOrCaloriesIndex(0)
                // CounterUI: set the segmentedButton to the selection 'Sugar'
                counterVM.actionChangeSegmentedButtonIndex(0)
            }
        )
    }

}