package com.example.mva_sugarcounter.composables

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mva_sugarcounter.R
import com.example.mva_sugarcounter.data.GraphData
import com.example.mva_sugarcounter.util.HelperMethods
import com.example.mva_sugarcounter.viewModels.HistoryVM


@Composable
fun History(context: Context) {

    val helperMethods = HelperMethods(context)

    val historyVM: HistoryVM = viewModel()
    val savedSugarCountGrouped by historyVM.savedHistory.collectAsState()
    val historyChartScreenShown by historyVM.historyChartScreenShown.collectAsState()
    val historyCardsScreenShown by historyVM.historyCardsScreenShown.collectAsState()

    Column {

        // sets buttons with which the history screen can be selected: Graph or Cards
        ChangeHistoryScreenSelection(historyVM)

        //Card Screen
        if (historyCardsScreenShown) {
            LazyColumn {
                items(
                    savedSugarCountGrouped.toList()
                        .sortedByDescending { it.first.first }) { (key, value) ->
                    ShowSugarCountItemsShared(key = key.second, valueList = value, false)
                }
            }
        }

        // Line Chart Screen
        if (historyChartScreenShown) {
            val graphDataList =
                savedSugarCountGrouped.values.toList().takeLast(35).mapIndexed { id, value ->
                GraphData(
                    id = id,
                    gramTotal = helperMethods.calculateTotalGramPerDayBlock(value),
                    day = helperMethods.formatDateToString(
                        value.first().currentTimestamp,
                        if (helperMethods.getSystemLanguage() == "en") {
                            "EEEE \n MM/dd"
                        } else {
                            "EEEE \n dd.MM"
                        }
                    )
                )
            }

            val graphDataListSorted = graphDataList.sortedByDescending { it.id }

            val darkMode = context.resources.configuration.uiMode
            LineChart(graphDataList = graphDataListSorted, darkMode = darkMode)
        }

    }
}

@Composable
fun ChangeHistoryScreenSelection(historyVM: HistoryVM) {

    Row {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp),
            onClick = {
            historyVM.actionHideHistoryCardsScreen()
            historyVM.actionShowHistoryChartScreen()
        }) {
            Text(stringResource(id = R.string.historygraphBtn))
        }

        Button(
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp),
            onClick = {
            historyVM.actionShowHistoryCardsScreen()
            historyVM.actionHideHistoryChartScreen()
        }) {
            Text(stringResource(R.string.historyCardsBtn))
        }

        IconButton(onClick = { historyVM.actionShowInfoBoxForHistoryScreen() }) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info"
            )
        }
    }

}



