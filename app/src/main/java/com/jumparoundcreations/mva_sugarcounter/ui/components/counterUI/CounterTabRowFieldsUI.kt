package com.jumparoundcreations.mva_sugarcounter.ui.components.counterUI

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
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.GeneralConstants

@Composable
fun CounterTabRowFieldsUI(
    onAction: (EntrySavingIntents) -> Unit,
    entrySavingStates: EntrySavingStates,
    accessibilityGramTextField: String,
    accessibilityGramTextFieldConsumed: String,
    labelGramField: String,
    labelQuantityField: String,
    onValueChangeGramField: (String) -> Unit,
    onValueChangeQuantityField: (String) -> Unit,
    quantityFieldPlaceholder: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .weight(1f),
            Arrangement.SpaceEvenly,
        ) {
            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                text = labelGramField,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )

            TextField(
                modifier =
                    Modifier.semantics {
                        contentDescription = accessibilityGramTextField
                    },
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor =
                            MaterialTheme.colorScheme.secondaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                value = if (entrySavingStates.gramCountMode == GramCountMode.PerHundred)
                    entrySavingStates.entryFieldGramPerHundred
                else entrySavingStates.entryFieldGramPerPiece,
                onValueChange = { input ->
                    onValueChangeGramField(input)
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        if (entrySavingStates.gramCountMode == GramCountMode.PerHundred) {
                            onAction(
                                EntrySavingIntents.ChangeEntryFieldGramPerHundred(
                                    entryFieldGramPerHundred = GeneralConstants.EMPTY_STRING,
                                ),
                            )
                        } else {
                            onAction(
                                EntrySavingIntents.ChangeEntryFieldGramPerPiece(
                                    entryFieldGramPerPiece = GeneralConstants.EMPTY_STRING,
                                ),
                            )
                        }

                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.accessibility_delete_value),
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                placeholder = {
                    Text(
                        text = stringResource(R.string.gram_unit_short),
                        modifier = Modifier.clearAndSetSemantics {},
                    )
                },
            )
        }

        Column(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .weight(1f),
            Arrangement.SpaceEvenly,
        ) {
            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                text = labelQuantityField,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )

            TextField(
                modifier =
                    Modifier.semantics {
                        contentDescription = accessibilityGramTextFieldConsumed
                    },
                colors =
                    OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor =
                            MaterialTheme.colorScheme.secondaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                value = entrySavingStates.entryFieldQuantity,
                onValueChange = { input ->
                    onValueChangeQuantityField(input)
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        onAction(
                            EntrySavingIntents.ChangeEntryFieldQuantity(
                                entryFieldQuantity = GeneralConstants.EMPTY_STRING,
                            ),
                        )
                    }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.accessibility_delete_value),
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                placeholder = {
                    Text(
                        text = quantityFieldPlaceholder,
                        modifier = Modifier.clearAndSetSemantics {},
                    )
                },
            )
        }
    }
}
