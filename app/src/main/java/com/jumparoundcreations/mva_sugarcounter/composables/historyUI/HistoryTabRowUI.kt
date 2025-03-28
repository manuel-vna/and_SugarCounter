package com.jumparoundcreations.mva_sugarcounter.composables.historyUI

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.SsidChart
import androidx.compose.material.icons.outlined.Tab
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.counterData.HistoryTabItem
import com.jumparoundcreations.mva_sugarcounter.viewModels.HistoryVM

@Composable
fun HistoryTabRowUI(historyVM: HistoryVM) {

    val historyTabItems = listOf(
        HistoryTabItem(
            index = 0,
            title = stringResource(id = R.string.historyCardsBtn),
            unselectedIcon = Icons.Outlined.Clear,
            selectedIcon = Icons.Outlined.Tab
        ),
        HistoryTabItem(
            index = 1,
            title = stringResource(id = R.string.historygraphBtn),
            unselectedIcon = Icons.Outlined.Clear,
            selectedIcon = Icons.Outlined.SsidChart
        )
    )

    val selectedTabIndex by historyVM.isCardTabIndex.collectAsState()
    val pagerState = rememberPagerState {
        historyTabItems.size
    }


    LaunchedEffect(key1 = selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(key1 = pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            historyVM.actionSetIsCardTabIndex(pagerState.currentPage)
    }


    TabRow(selectedTabIndex = selectedTabIndex) {
        historyTabItems.forEachIndexed { index, item ->
            Tab(selected = index == selectedTabIndex,
                onClick = {
                    historyVM.actionSetIsCardTabIndex(index)

                    if (index == 0) {
                        historyVM.actionShowHistoryCardsScreen()
                        historyVM.actionHideHistoryChartScreen()
                    } else {
                        historyVM.actionHideHistoryCardsScreen()
                        historyVM.actionShowHistoryChartScreen()
                        historyVM.actionChangeHistoryCardSearchFieldText("")
                    }

                },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (index == selectedTabIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = item.title)
                    }
                }
            )
        }
    }

}