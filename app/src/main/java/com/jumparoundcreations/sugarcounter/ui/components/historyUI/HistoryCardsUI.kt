package com.jumparoundcreations.sugarcounter.ui.components.historyUI

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.data.EntryGroup
import com.jumparoundcreations.sugarcounter.ui.components.ShowSharedCards
import com.jumparoundcreations.sugarcounter.ui.components.sharedUI.EmptyDataInfo
import com.jumparoundcreations.sugarcounter.viewModels.HistoryVM


@Composable
fun CardsScreen(
    historyVM: HistoryVM,
    savedSugarCountGrouped: List<EntryGroup>,
) {

    val historyCardSearchFieldShown by historyVM.historyCardSearchFieldShown.collectAsState()
    val historyCardSearchFieldText by historyVM.historyCardSearchFieldText.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {

            if (savedSugarCountGrouped.isNotEmpty()) {
                items(savedSugarCountGrouped) {
                    ShowSharedCards(
                        entryGroup = it,
                        backgroundColorPrimary = false
                    )
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxHeight()
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyDataInfo(stringResource(id = R.string.no_cards_yet_description))
                    }
                }
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomStart),
            verticalAlignment = Alignment.Bottom
        ) {
            FloatingActionButton(
                onClick = {
                    historyVM.actionChangeHistoryCardSearchFieldShown(
                        historyCardSearchFieldShown.not()
                    )
                },
                modifier = Modifier.padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.accessibility_search)
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = historyCardSearchFieldShown,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            ) {
                OutlinedTextField(
                    value = historyCardSearchFieldText,
                    onValueChange = { historyVM.actionChangeHistoryCardSearchFieldText(it) },
                    label = { Text("Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }
        }
    }

}