package com.jumparoundcreations.mva_sugarcounter.composables.counterUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterCaloriesVM
import org.koin.androidx.compose.koinViewModel


@Composable
fun RowScope.CounterCaloriesUI(
    caloriesCounterActivated: Boolean,
    //modifier: Modifier = Modifier
) {

    // States
    val counterCaloriesVM: CounterCaloriesVM = koinViewModel()
    val caloriesInput by counterCaloriesVM.caloriesInput.collectAsState()

    if (caloriesCounterActivated) {

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
        ) {

            Text(
                modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
                text = stringResource(id = R.string.calories_textfield_title),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            TextField(
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp),
                value = caloriesInput,
                onValueChange = {
                    if (it.isDigitsOnly() && it.count() <= 4) {
                        counterCaloriesVM.actionPerPieceGramChange(it)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 16.sp
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { counterCaloriesVM.actionPerPieceGramChange("") }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "arrow",
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text(stringResource(id = R.string.calories_textfield_placeholder)) }
            )
        }
    }
}