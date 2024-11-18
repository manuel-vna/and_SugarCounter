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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM

@Composable
fun CounterPerPiece(counterVM: CounterVM) {

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
                text = stringResource(R.string.gramSugar),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            val perPieceGram by counterVM.perPieceGram.collectAsState()
            TextField(
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor =
                    MaterialTheme.colorScheme.secondaryContainer
                ),
                value = perPieceGram,
                onValueChange = {
                    if (it.isDigitsOnly() && it.count() <= 3) counterVM.actionPerPieceGramChange(it)
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { counterVM.actionPerPieceGramChange("") }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "arrow",
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("g") }
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .weight(1f),
            Arrangement.SpaceEvenly
        ) {

            val perPieceAmount by counterVM.perPieceAmount.collectAsState()
            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                text = stringResource(R.string.quantitySugar),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            TextField(
                value = perPieceAmount,
                onValueChange = {
                    if (it.isDigitsOnly() && it.count() <= 3) counterVM.actionPerPieceAmountChange(
                        it
                    )
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { counterVM.actionPerPieceAmountChange("") }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "arrow",
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("1") }
            )
        }
    }

}