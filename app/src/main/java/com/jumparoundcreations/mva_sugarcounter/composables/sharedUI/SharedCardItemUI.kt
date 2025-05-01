package com.jumparoundcreations.mva_sugarcounter.composables.sharedUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.ShowAlertDialogDoubleBtn
import com.jumparoundcreations.mva_sugarcounter.data.Entry
import com.jumparoundcreations.mva_sugarcounter.data.EntryCalories
import com.jumparoundcreations.mva_sugarcounter.viewModels.SharedVM
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedCardItem(
    isEntrySugar: Boolean,
    entrySugar: Entry,
    entryCalories: EntryCalories
) {

    val sharedVM: SharedVM = koinViewModel()

    val valueCategory by sharedVM.valueCategory.collectAsState()
    val headingProportion by sharedVM.headingProportion.collectAsState()
    val headingConsumed by sharedVM.headingConsumed.collectAsState()
    val valueProportion by sharedVM.valueProportion.collectAsState()
    val valueConsumed by sharedVM.valueConsumed.collectAsState()

    // The fields of the bottom sheet are filled with the values from the entry that was clicked
    when (isEntrySugar) {
        true -> if (entrySugar.isPerHundred) {
            sharedVM.actionChangeValueCategory(entrySugar.category)
            sharedVM.actionChangeHeadingProportion(stringResource(id = R.string.gramPerHundredLabel))
            sharedVM.actionChangeHeadingConsumed(stringResource(id = R.string.amountSugar))
            sharedVM.actionChangeValueProportion(entrySugar.perHundredGram.toString())
            sharedVM.actionChangeValueConsumed(entrySugar.perHundredQuantity.toString())
        } else {
            sharedVM.actionChangeValueCategory(entrySugar.category)
            sharedVM.actionChangeHeadingProportion(stringResource(id = R.string.gramSugar))
            sharedVM.actionChangeHeadingConsumed(stringResource(id = R.string.quantitySugar))
            sharedVM.actionChangeValueProportion(entrySugar.perPieceGram.toString())
            sharedVM.actionChangeValueConsumed(entrySugar.perPieceAmount.toString())
        }

        else -> {
            sharedVM.actionChangeValueCategory(entryCalories.category)
            sharedVM.actionChangeHeadingProportion(stringResource(id = R.string.general_calories_lowercase))
            sharedVM.actionChangeHeadingConsumed(stringResource(id = R.string.quantitySugar))
            sharedVM.actionChangeValueProportion(entryCalories.caloriesPerPiece.toString())
            sharedVM.actionChangeValueConsumed(entryCalories.caloriesAmount.toString())
        }
    }

    ModalBottomSheet(
        modifier = Modifier.navigationBarsPadding(),
        onDismissRequest = { sharedVM.actionDismissCardItem() }
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
                    onValueChange = { sharedVM.actionChangeValueCategory(it) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { sharedVM.actionChangeValueCategory("") }) {
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
                        if (it.isDigitsOnly() && it.count() <= 3) sharedVM.actionChangeValueProportion(
                            it
                        )
                    },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { sharedVM.actionChangeValueProportion("") }) {
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
                        if (it.isDigitsOnly() && it.count() <= 3) sharedVM.actionChangeValueConsumed(
                            it
                        )
                    },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { sharedVM.actionChangeValueConsumed("") }) {
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
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
                    sharedVM.actionShowDialogEntryDeletionConfirmation(true)
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

                onClick = { sharedVM.actionDismissCardItem() }
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
                    sharedVM.actionEditDatabaseEntry(
                        isEntrySugar,
                        entrySugar,
                        entryCalories,
                        valueCategory,
                        valueProportion,
                        valueConsumed
                    )
                    sharedVM.actionDismissCardItem()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.general_edit)
                )
            }

        }

    }


    val dialogEntryDeletionConfirmation by sharedVM.dialogEntryDeletionConfirmation.collectAsState()

    if (dialogEntryDeletionConfirmation) {
        ShowAlertDialogDoubleBtn(
            dialogTitle = if (isEntrySugar) entrySugar.category else entryCalories.category,
            dialogDescription = stringResource(id = R.string.general_delete_question),
            confirmBtnText = stringResource(id = R.string.general_delete),
            confirmBtnAction = {
                val itemToDelete =
                    if (isEntrySugar) entrySugar else entryCalories
                sharedVM.actionDeleteSpecificEntryRow(
                    itemToDeleteIsEntrySugar = isEntrySugar,
                    id = itemToDelete.id
                )
                sharedVM.actionShowDialogEntryDeletionConfirmation(false)
                sharedVM.actionDismissCardItem()
            },
            dismissBtnText = stringResource(R.string.generalCancel),
            dismissBtnAction = {
                sharedVM.actionShowDialogEntryDeletionConfirmation(false)
            },
            onDismissRequest = { sharedVM.actionShowDialogEntryDeletionConfirmation(false) }
        )
    }

}




