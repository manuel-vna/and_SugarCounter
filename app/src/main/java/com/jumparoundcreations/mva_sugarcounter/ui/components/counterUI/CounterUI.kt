package com.jumparoundcreations.mva_sugarcounter.ui.components.counterUI

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.Screens
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingStates
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingViewModel
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingViewModel
import com.jumparoundcreations.mva_sugarcounter.ui.components.entryListUI.EntryListBottomSheet
import com.jumparoundcreations.mva_sugarcounter.ui.components.entryListUI.entryListItems
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun Counter(
    context: Context,
    snackbarHostState: SnackbarHostState,
    entrySavingViewModel: EntrySavingViewModel = koinViewModel(),
    entryListDisplayingViewModel: EntryListDisplayingViewModel,
    sharedPrefsMain: SharedPreferences = koinInject(),
) {
    // States
    val interactionSource = remember { MutableInteractionSource() }
    val entrySavingStates by entrySavingViewModel.entrySavingStates.collectAsStateWithLifecycle()
    val entryListDisplayingStates by
        entryListDisplayingViewModel.entryListDisplayingStates.collectAsStateWithLifecycle()

    // Keyboard
    val keyboardController = LocalSoftwareKeyboardController.current

    // Style
    val darkMode = HelperMethods.checkForUIMode(context)
    val textColor = if (darkMode == 33) Color.White else Color.Black

    LazyColumn(
        modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        entrySavingViewModel.onAction(
                            action =
                                EntrySavingIntents.ExpandOrCollapseCategoryDropdown(
                                    categoryDropdownExpanded = false,
                                ),
                        )
                    },
                ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp, top = 8.dp),
                Arrangement.Absolute.SpaceAround,
            ) {
                DatePicker(
                    onAction = entrySavingViewModel::onAction,
                    entrySavingStates = entrySavingStates,
                    textColor = textColor,
                )

                Barcode(
                    onAction = entrySavingViewModel::onAction,
                    textColor = textColor,
                )
            }
        }

        item {
            if (entrySavingStates.barcodeNotPresentInDb) {
                BarcodeInfoSheet(
                    onAction = entrySavingViewModel::onAction,
                    states = entrySavingStates,
                    barcodeNumber = entrySavingStates.barcodeNumber,
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                CategoryDropdownField(
                    context = context,
                    onAction = entrySavingViewModel::onAction,
                    entrySavingStates = entrySavingStates,
                    keyboardController = keyboardController,
                )
            }
        }

        item {
            TabRow(
                onAction = entrySavingViewModel::onAction,
                entrySavingStates = entrySavingStates,
            )
        }

        item {
            Button(
                modifier =
                    Modifier
                        .padding(vertical = 8.dp)
                        .width(160.dp),
                onClick = {
                    entrySavingViewModel.onAction(EntrySavingIntents.SaveSugarEntry)
                    // clearing fields by an empty value / setting fields back to default
                    entrySavingViewModel.onAction(EntrySavingIntents.ClearInputFields)
                    keyboardController?.hide()
                },
            ) {
                Text(
                    text = stringResource(id = R.string.saveButton),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        }

        when (val states = entryListDisplayingStates) {
            is EntryListDisplayingStates.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxHeight(0.4f).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(90.dp),
                        )
                    }
                }
            }

            is EntryListDisplayingStates.Success -> {
                entryListItems(
                    currentScreen = Screens.COUNTER,
                    data = states.data,
                    onAction = entryListDisplayingViewModel::onAction,
                    sharedPrefsMain = sharedPrefsMain
                )
            }

            is EntryListDisplayingStates.Error -> {
                item {
                    Text(
                        text = "Error: " + states.message,
                    )
                }
            }
        }
    }

    if (entryListDisplayingStates is EntryListDisplayingStates.Success) {
        EntryListBottomSheet(
            data = (entryListDisplayingStates as EntryListDisplayingStates.Success).data,
            onAction = entryListDisplayingViewModel::onAction
        )
    }

    CounterUserInformation(
        context = context,
        entrySavingStates = entrySavingStates,
        onAction = entrySavingViewModel::onAction,
        scanUiEvents = entrySavingViewModel.scanUiEvents,
        snackbarHostState = snackbarHostState,
    )
}
