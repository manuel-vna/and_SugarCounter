package com.jumparoundcreations.mva_sugarcounter.composables.historyUI

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.GraphData
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.viewModels.HistoryVM
import org.koin.androidx.compose.koinViewModel


@Composable
fun History(context: Context) {

    val historyVM: HistoryVM = koinViewModel()
    val savedSugarCountGrouped by historyVM.savedHistory.collectAsState()
    val historyChartScreenShown by historyVM.historyChartScreenShown.collectAsState()
    val historyCardsScreenShown by historyVM.historyCardsScreenShown.collectAsState()
    val historyInfoDialogShown by historyVM.historyInfoDialogShown.collectAsState()


    if (historyInfoDialogShown) {
        InfoDialog(historyVM, historyChartScreenShown)
    }

    Column {

        // Area at the top: Buttons and info icon
        HistoryScreenTopArea(historyVM)

        //Card Screen
        if (historyCardsScreenShown) {
            CardsScreen(historyVM = historyVM, savedSugarCountGrouped = savedSugarCountGrouped)
        }

        // Line Chart Screen
        if (historyChartScreenShown) {
            val graphDataList =
                savedSugarCountGrouped.take(60).mapIndexed { id, entryGroup ->
                    GraphData(
                        id = id,
                        gramTotal = HelperMethods.calculateTotalGramPerDayBlock(entryGroup.entryList),
                        day = HelperMethods.formatDateToString(
                            entryGroup.entryList.first().currentTimestamp,
                            if (HelperMethods.getSystemLanguage() == "en") {
                                "EEEE \n MM/dd"
                            } else {
                                "EEEE \n dd.MM"
                            }
                        ),
                        date = entryGroup.date
                    )
                }

            val graphDataListSorted = graphDataList.sortedByDescending { it.date }

            val darkMode = HelperMethods.checkForUIMode(context)
            LineChart(graphDataList = graphDataListSorted, darkMode = darkMode)
        }
    }
}

@Composable
fun HistoryScreenTopArea(historyVM: HistoryVM) {

    Row {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp),
            onClick = {
                historyVM.actionHideHistoryCardsScreen()
                historyVM.actionShowHistoryChartScreen()
                historyVM.actionChangeHistoryCardSearchFieldText("")
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
    }

    // Info button for diagram and cards history screens
    /*
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End

    ) {
        IconButton(onClick = { historyVM.actionShowInfoBoxForHistoryScreen() }) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info"
            )
        }
    }
     */

}


@Composable
fun InfoDialog(historyVM: HistoryVM, historyChartScreenShown: Boolean) {

    Dialog(onDismissRequest = { historyVM.actionDismissInfoDialog() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier
                        .weight(1f),
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info"
                )
                Text(
                    modifier = Modifier
                        .weight(5f)
                        .padding(16.dp),
                    fontSize = 14.sp,
                    text = if (historyChartScreenShown) {
                        stringResource(R.string.historyInfoDescriptionGraph)
                    } else {
                        stringResource(R.string.historyInfoDescriptionCards)
                    },
                )
                TextButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = { historyVM.actionDismissInfoDialog() },
                ) {
                    Text(stringResource(R.string.generalClose))
                }
            }
        }
    }
}



