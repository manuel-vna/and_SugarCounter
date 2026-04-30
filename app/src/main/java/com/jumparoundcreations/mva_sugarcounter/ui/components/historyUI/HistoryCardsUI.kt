package com.jumparoundcreations.mva_sugarcounter.ui.components.historyUI

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.Screens
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.SuccessData
import com.jumparoundcreations.mva_sugarcounter.ui.components.entryListUI.EntryListUI


@Composable
fun CardsScreen(
    onAction: (EntryListDisplayingIntents) -> Unit,
    data: SuccessData
) {

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            item {
                EntryListUI(
                    currentScreen = Screens.HISTORY,
                    backgroundColorPrimary = false,
                    data = data,
                    onAction = onAction
                )
            }

        }

        Row(
            modifier = Modifier.align(Alignment.BottomStart),
            verticalAlignment = Alignment.Bottom
        ) {
            FloatingActionButton(
                onClick = {
                    onAction(
                        EntryListDisplayingIntents.ChangeSearchFieldShown
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
                visible = data.searchFieldShown,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            ) {
                OutlinedTextField(
                    value = data.searchFieldText,
                    onValueChange = {
                        onAction(
                            EntryListDisplayingIntents.ChangeSearchTextFieldText(
                                newText = it
                            )
                        )
                        onAction(EntryListDisplayingIntents.FilterEntryListInHistory)
                    },
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