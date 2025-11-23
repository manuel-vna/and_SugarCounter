package com.jumparoundcreations.sugarcounter.ui.components.counterUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingViewModel
import com.jumparoundcreations.sugarcounter.util.NumberConstants

@Composable
fun CounterTabRowFieldsUI(
    entrySavingViewModel: EntrySavingViewModel,
    accessibilityGramTextField: String,
    accessibilityGramTextFieldConsumed: String,
    onValueChangeGramField: (String) -> Unit,
    onValueChangeQuantityField: (String) -> Unit,
    quantityFieldPlaceholder: String
) {

    val entrySavingStates by entrySavingViewModel.entrySavingStates.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .weight(1f),
            Arrangement.SpaceEvenly
        ) {

            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                text = stringResource(R.string.gramPerHundredLabel),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            TextField(
                modifier = Modifier.semantics {
                    contentDescription = accessibilityGramTextField
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor =
                        MaterialTheme.colorScheme.secondaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                value = if (entrySavingStates.entryFieldGram == NumberConstants.NULL_AS_DOUBLE) ""
                else entrySavingStates.entryFieldGram.toString(),
                onValueChange = { input ->
                    onValueChangeGramField(input)
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        entrySavingViewModel.onAction(
                            action = EntrySavingIntents.ChangeEntryFieldGram(
                                entryFieldGram = NumberConstants.NULL_AS_DOUBLE
                            )
                        )
                    }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.accessibility_delete_value),
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = {
                    Text(
                        text = "g",
                        modifier = Modifier.clearAndSetSemantics {}
                    )
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .weight(1f),
            Arrangement.SpaceEvenly
        ) {

            //val amountValue by counterVM.perHundredQuantity.collectAsState()
            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                text = stringResource(R.string.amountSugar),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            TextField(
                modifier = Modifier.semantics {
                    contentDescription = accessibilityGramTextFieldConsumed
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor =
                        MaterialTheme.colorScheme.secondaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                value = if (
                    entrySavingStates.entryFieldQuantity == NumberConstants.NULL_AS_DOUBLE
                ) ""
                else entrySavingStates.entryFieldQuantity.toString(),
                onValueChange = { input ->
                    onValueChangeQuantityField(input)
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        entrySavingViewModel.onAction(
                            action = EntrySavingIntents.ChangeEntryFieldQuantity(
                                entryFieldQuantity = NumberConstants.NULL_AS_DOUBLE
                            )
                        )
                    }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.accessibility_delete_value),
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = {
                    Text(
                        text = quantityFieldPlaceholder,
                        modifier = Modifier.clearAndSetSemantics {}
                    )
                }
            )
        }
    }

}
