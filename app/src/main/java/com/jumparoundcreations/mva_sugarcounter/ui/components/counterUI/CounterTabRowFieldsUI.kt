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

@Composable
fun CounterTabRowFieldsUI(
    valueGram: String,
    valueQuantity: String,
    accessibilityGramTextField: String,
    accessibilityGramTextFieldConsumed: String,
    labelGramField: String,
    labelQuantityField: String,
    onValueChangeGramField: (String) -> Unit,
    onValueChangeQuantityField: (String) -> Unit,
    onClearGramField: () -> Unit,
    onClearQuantityField: () -> Unit,
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
                value = valueGram,
                onValueChange = { input ->
                    onValueChangeGramField(input)
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = onClearGramField
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
                value = valueQuantity,
                onValueChange = { input ->
                    onValueChangeQuantityField(input)
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = onClearQuantityField ) {
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
