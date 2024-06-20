package com.example.mva_sugarcounter.composables.counterUI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.Home
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
import com.example.mva_sugarcounter.data.TabItem
import com.example.mva_sugarcounter.viewModels.CounterVM


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabRow(counterVM: CounterVM) {

    val tabItems = listOf(
        TabItem(
            title = "A",
            unselectedIcon = Icons.Outlined.Abc,
            selectedIcon = Icons.Outlined.Abc
        ),
        TabItem(
            title = "B",
            unselectedIcon = Icons.Outlined.Home,
            selectedIcon = Icons.Outlined.Home
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
    Column(
        //modifier = Modifier.fillMaxSize()
    ) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabItems.forEachIndexed { index, item ->
                Tab(selected = index == selectedTabIndex,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Text(text = item.title)
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == selectedTabIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                //.fillMaxWidth()
                .weight(1f)
        ) { index ->
            Box(
                //modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                if (tabItems[index].title == "A") {
                    CounterPerPiece(counterVM)
                } else {
                    Text(text = tabItems[index].title)
                }
            }


        }
    }

}