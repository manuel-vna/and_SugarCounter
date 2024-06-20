package com.example.mva_sugarcounter.composables.counterUI

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
import com.example.mva_sugarcounter.R
import com.example.mva_sugarcounter.viewModels.CounterVM

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
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            val gramValue by counterVM.gramValue.collectAsState()
            TextField(
                value = gramValue,
                onValueChange = {
                    if (it.isDigitsOnly() && it.count() <= 3) counterVM.actionGramChange(it)
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { counterVM.actionGramChange("") }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "arrow",
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .weight(1f),
            Arrangement.SpaceEvenly
        ) {

            val amountValue by counterVM.amountValue.collectAsState()
            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                text = stringResource(R.string.quantitySugar),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            TextField(
                value = amountValue,
                onValueChange = {
                    if (it.isDigitsOnly() && it.count() <= 3) counterVM.actionAmountChange(
                        it
                    )
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { counterVM.actionAmountChange("") }) {
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