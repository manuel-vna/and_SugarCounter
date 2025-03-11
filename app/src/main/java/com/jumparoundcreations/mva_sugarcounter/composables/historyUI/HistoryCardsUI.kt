package com.jumparoundcreations.mva_sugarcounter.composables.historyUI

import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.composables.ShowSharedCards
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.viewModels.HistoryVM


@Composable
fun CardsScreen(
    historyVM: HistoryVM,
    savedSugarCountGrouped: List<EntryGroup<Entry>>,
    caloriesEntryDbHistory: List<EntryGroup<EntryCalories>>
) {

    val historyCardSearchFieldShown by historyVM.historyCardSearchFieldShown.collectAsState()
    val historyCardSearchFieldText by historyVM.historyCardSearchFieldText.collectAsState()
    val segmentedButtonIndex by historyVM.segmentedButtonIndex.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {

            when (segmentedButtonIndex) {
                0 -> {
                    items(savedSugarCountGrouped) {
                        ShowSharedCards(
                            entryGroup = it,
                            backgroundColorPrimary = false
                        )
                    }
                }

                1 -> {
                    Log.d("HistoryCardsUI.kt", "Calories tab chosen")

                    items(caloriesEntryDbHistory) {
                        ShowSharedCards(
                            entryGroup = it,
                            backgroundColorPrimary = false
                        )
                    }

                }

                else -> Log.d("HistoryCardsUI.kt", "No index found")
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
                Icon(Icons.Default.Search, contentDescription = "Search")
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