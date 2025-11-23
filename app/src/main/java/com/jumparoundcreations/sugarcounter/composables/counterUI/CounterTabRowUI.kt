package com.jumparoundcreations.sugarcounter.composables.counterUI

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingViewModel
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.CounterTabItem
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.sugarcounter.util.GeneralConstants
import com.jumparoundcreations.sugarcounter.viewModels.CounterVM


@Composable
fun TabRow(entrySavingViewModel: EntrySavingViewModel, counterVM: CounterVM) {

    val tabItems = listOf(
        CounterTabItem(
            gramCountMode = GramCountMode.PerHundred,
            title = stringResource(id = R.string.tabCounterPerHundred),
            unselectedIcon = Icons.Outlined.BarChart,
            selectedIcon = Icons.Outlined.BarChart,
            contentDescription = stringResource(id = R.string.accessibility_tabCounterPerHundred),
        ),
        CounterTabItem(
            gramCountMode = GramCountMode.PerPiece,
            title = stringResource(id = R.string.tabCounterPerPiece),
            unselectedIcon = Icons.Outlined.Dialpad,
            selectedIcon = Icons.Outlined.Dialpad,
            contentDescription = stringResource(id = R.string.accessibility_tabCounterPerPiece),
        )
    )

    val selectedTabIndex by counterVM.isHundredTabIndex.collectAsState()
    val pagerState = rememberPagerState {
        tabItems.size
    }

    LaunchedEffect(key1 = selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(key1 = pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            counterVM.actionSetIsHundredTabIndex(pagerState.currentPage) //selectedTabIndex = pagerState.currentPage
    }

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = {
                        counterVM.actionSetIsHundredTabIndex(index) //selectedTabIndex = index
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
                                contentDescription = item.contentDescription
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

                if (tabItems[index].gramCountMode == GramCountMode.PerPiece) {

                    CounterTabRowFieldsUI(
                        entrySavingViewModel = entrySavingViewModel,
                        accessibilityGramTextField =
                            stringResource(R.string.accessibility_perPiece_textField),
                        accessibilityGramTextFieldConsumed =
                            stringResource(R.string.accessibility_perPiece_textField_consumed),
                        onValueChangeGramField = { input ->
                            if (input.isDigitsOnly() && input.count() <= 3)
                                counterVM.actionPerPieceGramChange(input)
                        },
                        onValueChangeQuantityField = { input ->
                            if (input.isDigitsOnly() && input.count() <= 2) counterVM.actionPerPieceAmountChange(
                                input
                            )
                        },
                        quantityFieldPlaceholder = GeneralConstants.ONE_AS_INT.toString(),
                    )

                    counterVM.actionChangeGramCountMode(GramCountMode.PerPiece)

                } else {

                    CounterTabRowFieldsUI(
                        entrySavingViewModel = entrySavingViewModel,
                        accessibilityGramTextField =
                            stringResource(R.string.accessibility_perHundredGram_textField),
                        accessibilityGramTextFieldConsumed =
                            stringResource(R.string.accessibility_perHundredGram_textField_consumed),
                        onValueChangeGramField =
                            { input ->
                                if (input.isDigitsOnly()) {
                                    val number = input.toIntOrNull()
                                    if (number != null && number in 1..100) {
                                        entrySavingViewModel.onAction(
                                            action = EntrySavingIntents.ChangeEntryFieldGram(
                                                entryFieldGram = input.toDouble()
                                            )
                                        )
                                    }
                                }
                            },
                        onValueChangeQuantityField = { input ->
                            if (input.isDigitsOnly() && input.count() <= 3)
                                entrySavingViewModel.onAction(
                                    action = EntrySavingIntents.ChangeEntryFieldQuantity(
                                        entryFieldQuantity = input.toDouble()
                                    )
                                )
                        },
                        quantityFieldPlaceholder = stringResource(R.string.gram_unit_short),
                    )

                    counterVM.actionChangeGramCountMode(GramCountMode.PerHundred)
                }
            }
        }
    }

}

}