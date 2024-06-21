package com.example.mva_sugarcounter.composables.counterUI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mva_sugarcounter.R
import com.example.mva_sugarcounter.data.TabId
import com.example.mva_sugarcounter.data.TabItem
import com.example.mva_sugarcounter.viewModels.CounterVM


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabRow(counterVM: CounterVM) {

    val tabItems = listOf(
        TabItem(
            tabId = TabId.PerHundred,
            title = stringResource(id = R.string.tabCounterPerHundred),
            unselectedIcon = Icons.Outlined.BarChart,
            selectedIcon = Icons.Outlined.BarChart
        ),
        TabItem(
            tabId = TabId.PerPiece,
            title = stringResource(id = R.string.tabCounterPerPiece),
            unselectedIcon = Icons.Outlined.Dialpad,
            selectedIcon = Icons.Outlined.Dialpad
        )
    )

    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    var pagerState = rememberPagerState {
        tabItems.size
    }
    LaunchedEffect(key1 = selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(key1 = pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            selectedTabIndex = pagerState.currentPage
    }
    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabItems.forEachIndexed { index, item ->
                Tab(selected = index == selectedTabIndex,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                imageVector = if (index == selectedTabIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                            Text(text = item.title)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
        ) { index ->
            Row {

                if (tabItems[index].tabId == TabId.PerPiece) {
                    CounterPerPiece(counterVM = counterVM)
                } else {
                    CounterPerHundred(counterVM = counterVM)
                }
            }
        }
    }

}