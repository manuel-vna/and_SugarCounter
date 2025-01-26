package com.jumparoundcreations.mva_sugarcounter.composables.historyUI

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.viewModels.HistoryVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named


@Composable
fun History(
    context: Context,
    sharedPrefsMain: SharedPreferences = koinInject(
        qualifier = named("sharedPrefsMain")
    )
) {

    val historyVM: HistoryVM = koinViewModel()
    val segmentedButtonIndex by historyVM.segmentedButtonIndex.collectAsState()
    val savedSugarCountGrouped by historyVM.savedHistory.collectAsState()
    val caloriesEntryDbHistory by historyVM.caloriesEntryDbHistory.collectAsState()
    val historyChartScreenShown by historyVM.historyChartScreenShown.collectAsState()
    val historyCardsScreenShown by historyVM.historyCardsScreenShown.collectAsState()
    val caloriesCounterActivated = sharedPrefsMain.getBoolean(
        "caloriesCounterActivated",
        false
    )

    Column {

        // Area at the top: Buttons and info icon
        HistoryScreenTopArea(historyVM, caloriesCounterActivated)

        //Card Screen
        if (historyCardsScreenShown) {
            CardsScreen(
                historyVM = historyVM,
                savedSugarCountGrouped = savedSugarCountGrouped,
                caloriesEntryDbHistory = caloriesEntryDbHistory
            )
        }

        // Line Chart Screen
        if (historyChartScreenShown) {
            LineChart(
                context = context,
                countMode = if (segmentedButtonIndex == 0) {
                    HelperMethods.CountMode.SUGAR
                } else {
                    HelperMethods.CountMode.CALORIES
                },
                sugarEntryDbHistory = savedSugarCountGrouped,
                caloriesEntryDbHistory = caloriesEntryDbHistory
            )
        }
    }
}

@Composable
fun HistoryScreenTopArea(
    historyVM: HistoryVM,
    caloriesCounterActivated: Boolean
) {

    Row {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp, end = 20.dp),
            onClick = {
                historyVM.actionShowHistoryCardsScreen()
                historyVM.actionHideHistoryChartScreen()
            }) {
            Text(stringResource(R.string.historyCardsBtn))
        }

        Button(
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp, end = 20.dp),
            onClick = {
                historyVM.actionHideHistoryCardsScreen()
                historyVM.actionShowHistoryChartScreen()
                historyVM.actionChangeHistoryCardSearchFieldText("")
            }) {
            Text(stringResource(id = R.string.historygraphBtn))
        }
    }

    if (caloriesCounterActivated) {

        val buttonOptions = listOf(
            stringResource(id = R.string.general_sugar),
            stringResource(id = R.string.general_calories)
        )
        var selectedIndex by remember { mutableIntStateOf(0) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            Arrangement.SpaceEvenly
        ) {
            SingleChoiceSegmentedButtonRow {
                buttonOptions.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            historyVM.actionChangeSegmentedButtonIndex(index)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = 2)
                    )
                    {
                        Text(
                            text = option
                        )
                    }

                }
            }
        }
    }

}





