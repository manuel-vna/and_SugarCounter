package com.example.mva_sugarcounter.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mva_sugarcounter.viewModels.CounterVM
import com.example.mva_sugarcounter.viewModels.HistoryVM


@Composable
fun History() {

    val counterVM: CounterVM = viewModel()
    val savedSugarCountGrouped by counterVM.savedHistory.collectAsState()

    val historyVM: HistoryVM = viewModel()
    val historyChartScreenShown by historyVM.historyChartScreenShown.collectAsState()
    val historyCardsScreenShown by historyVM.historyCardsScreenShown.collectAsState()

    Column {

        // sets buttons with which the history screen can be selected: Graph or Cards
        ChangeHistoryScreenSelection(historyVM)

        if (historyCardsScreenShown) {
            LazyColumn {
                items(
                    savedSugarCountGrouped.toList()
                        .sortedByDescending { it.first.first }) { (key, value) ->
                    ShowSugarCountItemsShared(key = key.second, valueList = value, false)
                }
            }
        }

        if (historyChartScreenShown) {

            val exampleData = listOf(
                Triple(0, 45, "2024-05-1"),
                Triple(1, 5, "2024-05-2"),
                Triple(2, 40, "2024-05-3"),
                Triple(3, 60, "2024-05-4"),
                Triple(4, 150, "2024-05-5"),
                Triple(5, 100, "2024-05-6"),
                Triple(6, 45, "2024-05-7")
            )

            SimpleLine(exampleDate = exampleData)
        }
    }
}

@Composable
fun ChangeHistoryScreenSelection(historyVM: HistoryVM) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly

    ) {
        Button(onClick = {
            historyVM.actionHideHistoryCardsScreen()
            historyVM.actionShowHistoryChartScreen()
        }) {
            Text("Graph")
        }

        Button(onClick = {
            historyVM.actionShowHistoryCardsScreen()
            historyVM.actionHideHistoryChartScreen()
        }) {
            Text("Cards")
        }
    }

}



