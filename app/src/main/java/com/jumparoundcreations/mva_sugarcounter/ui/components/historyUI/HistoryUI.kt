package com.jumparoundcreations.mva_sugarcounter.ui.components.historyUI

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature.EntryGraphDisplayingViewModel
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingStates
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingViewModel
import com.jumparoundcreations.mva_sugarcounter.ui.components.calendarUI.EntryCalendarFeature
import com.jumparoundcreations.mva_sugarcounter.viewModels.HistoryVM
import org.koin.androidx.compose.koinViewModel

@Composable
fun History(
    context: Context,
    entryListDisplayingViewModel: EntryListDisplayingViewModel,
    entryGraphDisplayingViewModel: EntryGraphDisplayingViewModel = koinViewModel(),
    historyViewModel: HistoryVM = koinViewModel(),
) {
    val entryListDisplayingStates by
        entryListDisplayingViewModel.entryListDisplayingStates.collectAsStateWithLifecycle()
    val entryGraphDisplayingStates by
        entryGraphDisplayingViewModel.entryGraphDisplayingStates.collectAsStateWithLifecycle()
    val historyTabOneShown by historyViewModel.historyTabOneShown.collectAsState()
    val historyTabTwoShown by historyViewModel.historyTabTwoShown.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        HistoryTabRowUI(historyViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        if (historyTabOneShown) {
            val states = entryListDisplayingStates
            when (states) {
                is EntryListDisplayingStates.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(90.dp),
                        )
                    }
                }

                is EntryListDisplayingStates.Success -> {
                    BackHandler(
                        enabled = states.data.searchFieldShown,
                    ) {
                        entryListDisplayingViewModel.onAction(
                            action = EntryListDisplayingIntents.CloseSearchFieldAndClearText,
                        )
                    }

                    CardsScreen(
                        onAction = entryListDisplayingViewModel::onAction,
                        data = states.data,
                    )
                }

                is EntryListDisplayingStates.Error -> {
                    Text(text = "Error: ${states.message}")
                }
            }
        }

        if (historyTabTwoShown) {

            EntryCalendarFeature()

            /*
            LineChartState(
                context,
                entryGraphDisplayingStates,
                isLandscape
            )
            */

        }
    }
}
