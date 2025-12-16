package com.jumparoundcreations.sugarcounter.ui.components.historyUI

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.ui.components.entryListUI.EmptyDataInfo
import com.jumparoundcreations.sugarcounter.util.HelperMethods
import com.jumparoundcreations.sugarcounter.viewModels.HistoryVM
import org.koin.compose.getKoin
import org.koin.compose.koinInject


@Composable
fun History(
    context: Context,
    sharedPrefsMain: SharedPreferences = koinInject()
) {
    val historyVM = getKoin().get<HistoryVM>()
    val segmentedButtonSugarOrCaloriesIndex by historyVM.segmentedButtonSugarOrCaloriesIndex.collectAsState()
    val savedSugarCountGrouped by historyVM.savedHistory.collectAsState()
    val historyChartScreenShown by historyVM.historyChartScreenShown.collectAsState()
    val historyCardsScreenShown by historyVM.historyCardsScreenShown.collectAsState()
    val caloriesCounterActivated = sharedPrefsMain.getBoolean(
        "caloriesCounterActivated",
        false
    )
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        HistoryScreenTopArea(
            historyVM,
            caloriesCounterActivated,
            segmentedButtonSugarOrCaloriesIndex
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Card Screen
        if (historyCardsScreenShown) {
            CardsScreen(
                historyVM = historyVM,
                savedSugarCountGrouped = savedSugarCountGrouped
            )
        }

        // Line Chart Screen
        if (historyChartScreenShown) {
            if (isLandscape) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EmptyDataInfo(stringResource(id = R.string.landscape_mode_no_graph_description))
                }
            } else {
                LineChart(
                    context = context,
                    countMode = if (segmentedButtonSugarOrCaloriesIndex == 0) {
                        HelperMethods.CountMode.SUGAR
                    } else {
                        HelperMethods.CountMode.CALORIES
                    },
                    sugarEntryDbHistory = savedSugarCountGrouped
                )
            }
        }
    }
}

@Composable
fun HistoryScreenTopArea(
    historyVM: HistoryVM,
    caloriesCounterActivated: Boolean,
    segmentedButtonSugarOrCaloriesIndex: Int
) {

    HistoryTabRowUI(historyVM)

    if (caloriesCounterActivated) {

        val buttonOptions = listOf(
            stringResource(id = R.string.general_sugar),
            stringResource(id = R.string.general_calories_uppercase)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            Arrangement.SpaceEvenly
        ) {
            SingleChoiceSegmentedButtonRow {
                buttonOptions.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = segmentedButtonSugarOrCaloriesIndex == index,
                        onClick = {
                            historyVM.actionChangeSegmentedButtonSugarOrCaloriesIndex(index)
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





