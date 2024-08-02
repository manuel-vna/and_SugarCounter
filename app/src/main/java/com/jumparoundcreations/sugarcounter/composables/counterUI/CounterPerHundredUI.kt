package com.jumparoundcreations.sugarcounter.composables.counterUI

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
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.viewModels.CounterVM

@Composable
fun CounterPerHundred(counterVM: CounterVM) {

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
                value = perHundredGram,
                onValueChange = {
                    if (it.isDigitsOnly() && it.count() <= 3) counterVM.actionPerHundredChange(it)
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { counterVM.actionPerHundredChange("") }) {
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

            val amountValue by counterVM.perHundredQuantity.collectAsState()
            Text(
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                text = stringResource(R.string.amountSugar),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            TextField(
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
                            contentDescription = "arrow",
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("g") }
            )
        }
    }

}