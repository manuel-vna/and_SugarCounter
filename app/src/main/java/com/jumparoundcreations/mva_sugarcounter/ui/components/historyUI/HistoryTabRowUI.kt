package com.jumparoundcreations.mva_sugarcounter.ui.components.historyUI

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Tab
import androidx.compose.material3.Icon
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.HistoryTabItem
import com.jumparoundcreations.mva_sugarcounter.viewModels.HistoryVM

@Composable
fun HistoryTabRowUI(historyVM: HistoryVM) {
    val historyTabItems =
        listOf(
            HistoryTabItem(
                index = 0,
                title = stringResource(id = R.string.historyCards),
                unselectedIcon = Icons.Outlined.Tab,
                selectedIcon = Icons.Outlined.Tab,
            ),
            HistoryTabItem(
                index = 1,
                title = stringResource(id = R.string.historyCalendar),
                unselectedIcon = Icons.Outlined.CalendarMonth,
                selectedIcon = Icons.Outlined.CalendarMonth,
            ),
        )

    val selectedTabIndex by historyVM.isCardTabIndex.collectAsState()

    SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
        historyTabItems.forEachIndexed { index, item ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = {
                    historyVM.actionSetIsCardTabIndex(index)
                },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector =
                                if (index == selectedTabIndex) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                            contentDescription = "",
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = item.title)
                    }
                },
            )
        }
    }
}
