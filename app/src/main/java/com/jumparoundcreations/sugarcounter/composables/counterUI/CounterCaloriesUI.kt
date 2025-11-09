package com.jumparoundcreations.sugarcounter.composables.counterUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.viewModels.CounterVM
import org.koin.compose.getKoin


@Composable
fun CounterCaloriesUI(
    caloriesCounterActivated: Boolean
) {

    // States
    val counterVM = getKoin().get<CounterVM>()
    val caloriesInput by counterVM.caloriesInput.collectAsState()
    val caloriesAmount by counterVM.caloriesAmount.collectAsState()

    if (caloriesCounterActivated) {

        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))

        Row {

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .weight(1f)
            ) {

                Text(
                    modifier = Modifier.padding(4.dp),
                    text = stringResource(id = R.string.general_calories_lowercase),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                TextField(
                    value = caloriesInput,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.count() <= 4) {
                            counterVM.actionCaloriesChange(it)
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = 14.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor =
                        MaterialTheme.colorScheme.secondaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { counterVM.actionCaloriesChange("") }) {
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

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .weight(1f)
            ) {

                val perPieceAmount by counterVM.perPieceAmount.collectAsState()
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = stringResource(R.string.quantitySugar),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                TextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor =
                        MaterialTheme.colorScheme.secondaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    value = caloriesAmount,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.count() <= 3) counterVM.actionCaloriesAmountChange(
                            it
                        )
                    },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { counterVM.actionCaloriesAmountChange("") }) {
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
}