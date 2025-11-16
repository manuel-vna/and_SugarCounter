package com.jumparoundcreations.sugarcounter.composables.cardsUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.composables.ShowAlertDialogDoubleBtn
import com.jumparoundcreations.sugarcounter.data.SugarEntry
import com.jumparoundcreations.sugarcounter.data.counterData.GramCountMode
import com.jumparoundcreations.sugarcounter.viewModels.CardsVM
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedCardItem(
    entrySugar: SugarEntry
) {

    val cardsVM: CardsVM = koinViewModel()

    val valueCategory by cardsVM.valueCategory.collectAsState()
    val headingProportion by cardsVM.headingProportion.collectAsState()
    val headingConsumed by cardsVM.headingConsumed.collectAsState()
    val valueProportion by cardsVM.valueProportion.collectAsState()
    val valueConsumed by cardsVM.valueConsumed.collectAsState()

    cardsVM.actionChangeValueCategory(entrySugar.category)
    cardsVM.actionChangeHeadingProportion(stringResource(id = R.string.gramSugar))
    cardsVM.actionChangeHeadingConsumed(stringResource(id = R.string.quantitySugar))
    cardsVM.actionChangeValueProportion(entrySugar.gram.toString())
    cardsVM.actionChangeValueConsumed(entrySugar.quantity.toString())


    ModalBottomSheet(
        modifier = Modifier
            .navigationBarsPadding(),
        onDismissRequest = { cardsVM.actionDismissCardItem() }
    ) {

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.card_item_detail_view_heading),
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.padding(8.dp)
                ) {

                    Text(
                        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                        text = stringResource(id = R.string.foodType),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    TextField(
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor =
                                MaterialTheme.colorScheme.secondaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        value = valueCategory,
                        onValueChange = { cardsVM.actionChangeValueCategory(it) },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { cardsVM.actionChangeValueCategory("") }) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "arrow",
                                )
                            }
                        },
                    )
                }
            }


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
                        text = headingProportion,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    TextField(
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor =
                                MaterialTheme.colorScheme.secondaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        value = valueProportion,
                        onValueChange = {
                            if (
                                it.isDigitsOnly()
                                &&
                                it.count() <= if (entrySugar.entryType == GramCountMode.PerHundred) 2 else 4
                            )
                                cardsVM.actionChangeValueProportion(it)
                        },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { cardsVM.actionChangeValueProportion("") }) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "arrow",
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text(stringResource(R.string.gram_unit_short)) }
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    Arrangement.SpaceEvenly
                ) {

                    Text(
                        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                        text = headingConsumed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    TextField(
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor =
                                MaterialTheme.colorScheme.secondaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        value = valueConsumed,
                        onValueChange = {
                            if (it.isDigitsOnly() && it.count() <= 3) cardsVM.actionChangeValueConsumed(
                                it
                            )
                        },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { cardsVM.actionChangeValueConsumed("") }) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "arrow",
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text(stringResource(R.string.gram_unit_short)) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ElevatedButton(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        cardsVM.actionReuseEntryForToday(
                            entrySugar,
                        )
                        cardsVM.actionDismissCardItem()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.card_item_detail_view_reuse_button)
                    )
                }
            }


            Row(
                modifier = Modifier.padding(
                    bottom = 32.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp, bottom = 12.dp)
                        .weight(0.1F),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White,
                        //contentColor for text font statically because it fits light and dark mode
                    ),
                    onClick = {
                        cardsVM.actionShowDialogEntryDeletionConfirmation(true)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.general_delete)
                    )
                }
                OutlinedButton(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp, bottom = 12.dp)
                        .weight(0.1f),

                    onClick = { cardsVM.actionDismissCardItem() }
                ) {
                    Text(
                        text = stringResource(R.string.generalCancel)
                    )
                }
                Button(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp, bottom = 12.dp)
                        .weight(0.1F),
                    onClick = {
                        /*
                        cardsVM.actionEditDatabaseEntry(
                            isEntrySugar,
                            entrySugar,
                            entryCalories,
                            valueCategory,
                            valueProportion,
                            valueConsumed
                        )
                         */
                        cardsVM.actionDismissCardItem()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.general_edit)
                    )
                }

            }
        }
    }


    val dialogEntryDeletionConfirmation by cardsVM.dialogEntryDeletionConfirmation.collectAsState()

    if (dialogEntryDeletionConfirmation) {
        ShowAlertDialogDoubleBtn(
            dialogTitle = entrySugar.category,
            dialogDescription = stringResource(id = R.string.general_delete_question),
            confirmBtnText = stringResource(id = R.string.general_delete),
            confirmBtnAction = {
                cardsVM.actionDeleteSpecificEntryRow(id = entrySugar.id)
                cardsVM.actionShowDialogEntryDeletionConfirmation(false)
                cardsVM.actionDismissCardItem()
            },
            dismissBtnText = stringResource(R.string.generalCancel),
            dismissBtnAction = {
                cardsVM.actionShowDialogEntryDeletionConfirmation(false)
            },
            onDismissRequest = { cardsVM.actionShowDialogEntryDeletionConfirmation(false) }
        )
    }

}




