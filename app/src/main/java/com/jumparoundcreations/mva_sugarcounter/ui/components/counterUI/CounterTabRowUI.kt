package com.jumparoundcreations.mva_sugarcounter.ui.components.counterUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CounterTabItem
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.ui.utils.InputFilters
import com.jumparoundcreations.mva_sugarcounter.util.GeneralConstants
import com.jumparoundcreations.mva_sugarcounter.util.NumberConstants

@Composable
fun TabRow(
    onAction: (EntrySavingIntents) -> Unit,
    entrySavingStates: EntrySavingStates,
) {
    val tabItems =
        listOf(
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
            ),
        )

    val pagerState = rememberPagerState { tabItems.size }

    LaunchedEffect(key1 = entrySavingStates.gramCountModeTabIndex) {
        pagerState.animateScrollToPage(entrySavingStates.gramCountModeTabIndex)
    }
    LaunchedEffect(key1 = pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            onAction(
                EntrySavingIntents.ChangeGramCountModeTabIndex(
                    tabIndex = pagerState.currentPage,
                ),
            )
        }
    }

    Column {
        PrimaryTabRow(selectedTabIndex = entrySavingStates.gramCountModeTabIndex) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = index == entrySavingStates.gramCountModeTabIndex,
                    onClick = {
                        onAction(
                            EntrySavingIntents.ChangeGramCountModeTabIndex(
                                tabIndex = index,
                            ),
                        )
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp),
                        ) {
                            Icon(
                                imageVector =
                                    if (index == entrySavingStates.gramCountModeTabIndex) {
                                        item.selectedIcon
                                    } else {
                                        item.unselectedIcon
                                    },
                                contentDescription = item.contentDescription,
                            )
                            Text(text = item.title)
                        }
                    },
                )
            }
        }

        Row {
            HorizontalPager(
                state = pagerState,
            ) { index ->

                val isPerHundred = tabItems[index].gramCountMode == GramCountMode.PerHundred

                CounterTabRowFieldsUI(
                    valueGram = if (isPerHundred) {
                        entrySavingStates.entryFieldGramPerHundred
                    } else {
                        entrySavingStates.entryFieldGramPerPiece
                    },
                    valueQuantity = if (isPerHundred) {
                        entrySavingStates.entryFieldQuantity
                    } else {
                        entrySavingStates.entryFieldAmount
                    },
                    accessibilityGramTextField = if (isPerHundred) {
                        stringResource(R.string.accessibility_perHundredGram_textField)
                    } else {
                        stringResource(R.string.accessibility_perPiece_textField)
                    },
                    accessibilityGramTextFieldConsumed = if (isPerHundred) {
                        stringResource(R.string.accessibility_perHundredGram_textField_consumed)
                    } else {
                        stringResource(R.string.accessibility_perPiece_textField_consumed)
                    },
                    labelGramField = if (isPerHundred) {
                        stringResource(R.string.gramPerHundredLabel)
                    } else {
                        stringResource(R.string.gramSugar)
                    },
                    labelQuantityField = if (isPerHundred) {
                        stringResource(R.string.amountSugar)
                    } else {
                        stringResource(R.string.quantitySugar)
                    },
                    onValueChangeGramField = { input ->
                        if (isPerHundred) {
                            if (InputFilters.filterBlockingOverHundred(input)) {
                                onAction(
                                    EntrySavingIntents.ChangeEntryFieldGramPerHundred(
                                        entryFieldGramPerHundred = input,
                                    ),
                                )
                            }
                        } else {
                            if (InputFilters.filterBlockingOverThousand(input)) {
                                onAction(
                                    EntrySavingIntents.ChangeEntryFieldGramPerPiece(
                                        entryFieldGramPerPiece = input,
                                    ),
                                )
                            }
                        }
                    },
                    onValueChangeQuantityField = { input ->
                        if (isPerHundred) {
                            if (InputFilters.filterBlockingOverThousand(input)) {
                                onAction(
                                    EntrySavingIntents.ChangeEntryFieldQuantity(
                                        entryFieldQuantity = input,
                                    ),
                                )
                            }
                        } else {
                            if (InputFilters.filterBlockingOverHundred(input)) {
                                onAction(
                                    EntrySavingIntents.ChangeEntryFieldAmount(
                                        entryFieldAmount = input,
                                    ),
                                )
                            }
                        }
                    },
                    onClearGramField = {
                        if (isPerHundred) {
                            onAction(
                                EntrySavingIntents.ChangeEntryFieldGramPerHundred(
                                    entryFieldGramPerHundred = GeneralConstants.EMPTY_STRING
                                )
                            )
                        } else {
                            onAction(
                                EntrySavingIntents.ChangeEntryFieldGramPerPiece(
                                    entryFieldGramPerPiece = GeneralConstants.EMPTY_STRING,
                                ),
                            )
                        }
                    },
                    onClearQuantityField = {
                        if (isPerHundred) {
                            onAction(
                                EntrySavingIntents.ChangeEntryFieldQuantity(
                                    entryFieldQuantity = GeneralConstants.EMPTY_STRING
                                ),
                            )
                        } else {
                            onAction(
                                EntrySavingIntents.ChangeEntryFieldAmount(
                                    entryFieldAmount = GeneralConstants.EMPTY_STRING
                                ),
                            )
                        }
                    },
                    quantityFieldPlaceholder = if (isPerHundred) {
                        stringResource(R.string.gram_unit_short)
                    } else {
                        NumberConstants.ONE_AS_INT.toString()
                    }
                )

            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(EntrySavingIntents.ChangeGramCountMode(tabItems[pagerState.currentPage].gramCountMode))
    }
}

