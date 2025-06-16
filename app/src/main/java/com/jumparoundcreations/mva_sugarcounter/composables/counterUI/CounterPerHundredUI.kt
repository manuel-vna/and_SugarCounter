package com.jumparoundcreations.mva_sugarcounter.composables.counterUI

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
import androidx.compose.runtime.collectAsState
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
import androidx.core.text.isDigitsOnly
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM

@Composable
fun CounterPerHundred(counterVM: CounterVM) {

    val accessibilityPerHundredGramTextField =
        stringResource(R.string.accessibility_perHundredGram_textField)
    val accessibilityPerHundredGramTextFieldConsumed =
        stringResource(R.string.accessibility_perHundredGram_textField_consumed)

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

            val perHundredGram by counterVM.perHundredGram.collectAsState()
            TextField(
                modifier = Modifier.semantics {
                    contentDescription = accessibilityPerHundredGramTextField
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor =
                        MaterialTheme.colorScheme.secondaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                value = perHundredGram,
                onValueChange = { input ->
                    if (input.isDigitsOnly()) {
                        val number = input.toIntOrNull()
                        if (number != null && number in 1..100) {
                            counterVM.actionPerHundredChange(input)
                        }
                    }
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { counterVM.actionPerHundredChange("") }) {
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

            val amountValue by counterVM.perHundredQuantity.collectAsState()
            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                text = stringResource(R.string.amountSugar),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            TextField(
                modifier = Modifier.semantics {
                    contentDescription = accessibilityPerHundredGramTextFieldConsumed
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor =
                        MaterialTheme.colorScheme.secondaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                value = amountValue,
                onValueChange = {
                    if (it.isDigitsOnly() && it.count() <= 3) counterVM.actionPerHundredQuantityChange(
                        it
                    )
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { counterVM.actionPerHundredQuantityChange("") }) {
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
    }

}