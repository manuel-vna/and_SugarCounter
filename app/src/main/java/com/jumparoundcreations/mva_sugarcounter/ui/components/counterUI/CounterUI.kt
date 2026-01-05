package com.jumparoundcreations.mva_sugarcounter.ui.components.counterUI

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature.EntryGraphDisplayingStates
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingStates
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingViewModel
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingViewModel
import com.jumparoundcreations.mva_sugarcounter.ui.components.entryListUI.EntryListUI
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import org.koin.androidx.compose.koinViewModel


@Composable
fun Counter(
    context: Context,
    snackbarHostState: SnackbarHostState,
    entrySavingViewModel: EntrySavingViewModel = koinViewModel(),
    entryListDisplayingViewModel: EntryListDisplayingViewModel = koinViewModel(),
) {

    // States
    val interactionSource = remember { MutableInteractionSource() }
    val entrySavingStates by entrySavingViewModel.entrySavingStates.collectAsStateWithLifecycle()
    val entryListDisplayingStates by
    entryListDisplayingViewModel.entryListDisplayingStates.collectAsStateWithLifecycle()

    //Keyboard
    val keyboardController = LocalSoftwareKeyboardController.current

    // Style
    val darkMode = HelperMethods.checkForUIMode(context)
    val textColor = if (darkMode == 33) Color.White else Color.Black


    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    entrySavingViewModel.onAction(
                        action = EntrySavingIntents.ExpandOrCollapseCategoryDropdown(
                            categoryDropdownExpanded = false
                        )
                    )
                }
            )
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            Arrangement.Absolute.SpaceAround
        ) {
            DatePicker(
                entrySavingViewModel = entrySavingViewModel,
                textColor = textColor,
            )

            Barcode(
                entrySavingViewModel = entrySavingViewModel,
                textColor = textColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (entrySavingStates.barcodeNotPresentInDb) {
                BarcodeInfoSheet(
                    onAction = entrySavingViewModel::onAction,
                    states = entrySavingStates,
                    barcodeNumber = entrySavingStates.barcodeNumber
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 4.dp)
        ) {

            CategoryDropdownField(
                context = context,
                entrySavingViewModel = entrySavingViewModel,
                keyboardController = keyboardController
            )
        }

        TabRow(entrySavingViewModel)

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = Modifier
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
                    fontSize = 16.sp
                )
            }

        }

        when (entryListDisplayingStates) {
            is EntryListDisplayingStates.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(90.dp)
                    )
                }
            }

            is EntryListDisplayingStates.Success -> {
                EntryListUI(
                    currentScreen = Screens.COUNTER,
                    backgroundColorPrimary = false,
                    data = (entryListDisplayingStates as EntryListDisplayingStates.Success).data
                )
            }

            is EntryListDisplayingStates.Error -> {
                Text(
                    text = "Error: " +
                            (entryListDisplayingStates as EntryGraphDisplayingStates.Error).message
                )
            }
        }

    }

    CounterUserInformation(
        context = context,
        entrySavingViewModel = entrySavingViewModel,
        snackbarHostState = snackbarHostState
    )

}