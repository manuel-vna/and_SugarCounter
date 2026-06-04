package com.jumparoundcreations.mva_sugarcounter.ui.components.entryListUI

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.SuccessData
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.ui.components.ShowAlertDialogDoubleBtn
import com.jumparoundcreations.mva_sugarcounter.ui.utils.InputFilters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryListItemUI(
    data: SuccessData,
    onAction: (EntryListDisplayingIntents) -> Unit,
) {
    LaunchedEffect(key1 = data.entryInCardItem.id) {
        onAction(
            EntryListDisplayingIntents.LoadEntryDataIntoCardDetails(
                sugarEntry = data.entryInCardItem,
            ),
        )
    }

    ModalBottomSheet(
        modifier =
            Modifier
                .navigationBarsPadding(),
        onDismissRequest = {
            onAction(EntryListDisplayingIntents.DismissCardDetails)
        },
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = stringResource(R.string.card_item_detail_view_heading),
                    fontWeight = FontWeight.Bold,
                )
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                        text = stringResource(id = R.string.foodType),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    TextField(
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor =
                                    MaterialTheme.colorScheme.secondaryContainer,
                                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        value = data.valueCategory,
                        onValueChange = {
                            onAction(
                                EntryListDisplayingIntents.EditCategory(
                                    newCategory = it,
                                ),
                            )
                        },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                onAction(
                                    EntryListDisplayingIntents.EditCategory(
                                        newCategory = "",
                                    ),
                                )
                            }) {
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
                        text =
                            if (data.entryInCardItem.entryType == GramCountMode.PerHundred) {
                                stringResource(id = R.string.gramPerHundredLabel)
                            } else {
                                stringResource(id = R.string.gramSugar)
                            },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    TextField(
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor =
                                    MaterialTheme.colorScheme.secondaryContainer,
                                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        value = data.valueGram,
                        onValueChange = {
                            if (InputFilters.filterBlockingOverHundred(
                                    input = it,
                                )
                            ) {
                                onAction(
                                    EntryListDisplayingIntents.EditGram(
                                        newGram = it,
                                    ),
                                )
                            }
                        },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                onAction(
                                    EntryListDisplayingIntents.EditGram(
                                        newGram = "",
                                    ),
                                )
                            }) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "arrow",
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text(stringResource(R.string.gram_unit_short)) },
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
                        text =
                            if (data.entryInCardItem.entryType == GramCountMode.PerHundred) {
                                stringResource(id = R.string.amountSugar)
                            } else {
                                stringResource(id = R.string.quantitySugar)
                            },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    TextField(
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor =
                                    MaterialTheme.colorScheme.secondaryContainer,
                                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        value = data.valueQuantity,
                        onValueChange = {
                            when (data.entryInCardItem.entryType) {
                                GramCountMode.PerHundred -> {
                                    if (InputFilters.filterBlockingOverThousand(
                                            input = it,
                                        )
                                    ) {
                                        onAction(
                                            EntryListDisplayingIntents.EditQuantity(
                                                newQuantity = it,
                                            ),
                                        )
                                    }
                                }

                                GramCountMode.PerPiece -> {
                                    if (InputFilters.filterBlockingOverHundred(
                                            input = it,
                                        )
                                    ) {
                                        onAction(
                                            EntryListDisplayingIntents.EditQuantity(
                                                newQuantity = it,
                                            ),
                                        )
                                    }
                                }
                            }
                        },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                onAction(
                                    EntryListDisplayingIntents.EditQuantity(
                                        newQuantity = "",
                                    ),
                                )
                            }) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "arrow",
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text(stringResource(R.string.gram_unit_short)) },
                    )
                }
            }

            Row(
                modifier =
                    Modifier
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                ElevatedButton(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        onAction(EntryListDisplayingIntents.ReuseEntryForToday)
                        onAction(EntryListDisplayingIntents.DismissCardDetails)
                    },
                ) {
                    Text(
                        text = stringResource(R.string.card_item_detail_view_reuse_button),
                    )
                }
            }

            Row(
                modifier =
                    Modifier.padding(
                        bottom = 32.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    modifier =
                        Modifier
                            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp)
                            .weight(0.1F),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.White,
                            // contentColor for text font statically because it fits light and dark mode
                        ),
                    onClick = {
                        onAction(
                            EntryListDisplayingIntents.ShowDeleteEntryConfirmation(
                                isShown = true,
                            ),
                        )
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.general_delete),
                    )
                }
                OutlinedButton(
                    modifier =
                        Modifier
                            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp)
                            .weight(0.1f),
                    onClick = {
                        onAction(EntryListDisplayingIntents.DismissCardDetails)
                    },
                ) {
                    Text(
                        text = stringResource(R.string.generalCancel),
                    )
                }
                Button(
                    modifier =
                        Modifier
                            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp)
                            .weight(0.1F),
                    onClick = {
                        onAction(EntryListDisplayingIntents.EditEntryInDB)
                        onAction(EntryListDisplayingIntents.DismissCardDetails)
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.general_edit),
                    )
                }
            }
        }
    }

    if (data.entryDeletionConfirmationDialogShown) {
        ShowAlertDialogDoubleBtn(
            dialogTitle = data.entryInCardItem.category,
            dialogDescription = stringResource(id = R.string.general_delete_question),
            confirmBtnText = stringResource(id = R.string.general_delete),
            confirmBtnAction = {
                onAction(
                    EntryListDisplayingIntents.DeleteEntry(
                        entryId = data.entryInCardItem.id,
                    ),
                )
                onAction(
                    EntryListDisplayingIntents.ShowDeleteEntryConfirmation(
                        isShown = false,
                    ),
                )
                onAction(EntryListDisplayingIntents.DismissCardDetails)
            },
            dismissBtnText = stringResource(R.string.generalCancel),
            dismissBtnAction = {
                onAction(
                    EntryListDisplayingIntents.ShowDeleteEntryConfirmation(
                        isShown = false,
                    ),
                )
            },
            onDismissRequest = {
                onAction(
                    EntryListDisplayingIntents.ShowDeleteEntryConfirmation(
                        isShown = false,
                    ),
                )
            },
        )
    }
}
