package com.jumparoundcreations.mva_sugarcounter.ui.components.historyUI

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
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.EntryGroupIntTemp
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingViewModel
import com.jumparoundcreations.mva_sugarcounter.ui.components.entryListUI.EmptyDataInfo
import com.jumparoundcreations.mva_sugarcounter.util.toIntModel
import com.jumparoundcreations.mva_sugarcounter.viewModels.HistoryVM
import org.koin.androidx.compose.koinViewModel


@Composable
fun History(
    context: Context,
    entryListDisplayingViewModel: EntryListDisplayingViewModel = koinViewModel(),
    historyViewModel: HistoryVM = koinViewModel()
) {

    val entryListDisplayingStates by entryListDisplayingViewModel.entryListDisplayingStates.collectAsStateWithLifecycle()
    val historyChartScreenShown by historyViewModel.historyChartScreenShown.collectAsState()
    val historyCardsScreenShown by historyViewModel.historyCardsScreenShown.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        HistoryTabRowUI(historyViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        //Card Screen
        if (historyCardsScreenShown) {
            CardsScreen(
                onAction = entryListDisplayingViewModel::onAction,
                states = entryListDisplayingStates
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





