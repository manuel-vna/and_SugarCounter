package com.jumparoundcreations.sugarcounter.ui.components.historyUI

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.data.EntryGroupIntTemp
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingViewModel
import com.jumparoundcreations.sugarcounter.ui.components.entryListUI.EmptyDataInfo
import com.jumparoundcreations.sugarcounter.util.toIntModel
import com.jumparoundcreations.sugarcounter.viewModels.HistoryVM
import org.koin.compose.getKoin


@Composable
fun History(
    context: Context,
) {

    val viewModel = getKoin().get<EntryListDisplayingViewModel>()
    val entryListDisplayingStates by viewModel.entryListDisplayingStates.collectAsStateWithLifecycle()

    val historyVM = getKoin().get<HistoryVM>()
    val historyChartScreenShown by historyVM.historyChartScreenShown.collectAsState()
    val historyCardsScreenShown by historyVM.historyCardsScreenShown.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        HistoryTabRowUI(historyVM)

        Spacer(modifier = Modifier.height(16.dp))

        //Card Screen
        if (historyCardsScreenShown) {
            CardsScreen(
                historyVM = historyVM
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
                val savedSugarCountGroupedInt: List<EntryGroupIntTemp> =
                    entryListDisplayingStates.entriesGroupedPerDayHistory.toIntModel()
                LineChart(
                    context = context,
                    sugarEntryDbHistory = savedSugarCountGroupedInt
                )
            }
        }
    }
}





