package com.jumparoundcreations.mva_sugarcounter.composables.counterUI


import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.ShowSugarCountItemsShared
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Counter(context: Context) {

    val counterVM: CounterVM = koinViewModel()

    val categories by counterVM.categories.collectAsState()
    val category by counterVM.categorySelected.collectAsState()

    var expanded by remember {
        mutableStateOf(false)
    }
    val heightTextFields by remember {
        mutableStateOf(55.dp)
    }
    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        modifier = Modifier
            .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
            .fillMaxWidth()

            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = false
                }
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            Arrangement.Absolute.SpaceAround
        ) {
            DatePicker(counterVM = counterVM)

            Barcode(counterVM)
        }

        Text(
            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
            text = stringResource(R.string.foodType),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Column(modifier = Modifier.fillMaxWidth()) {

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightTextFields)
                    .border(
                        width = 1.8.dp,
                        shape = RoundedCornerShape(15.dp),
                        color = Color.Transparent
                    )
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
                value = category,
                onValueChange = {
                    //limit input to 25 characters
                    if (it.count() <= 40) {
                        counterVM.actionChangeSelectedCategory(it)
                        expanded = true
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.categoryMaxInput),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                },
                textStyle = TextStyle(
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "arrow",
                        )
                    }
                }
            )

            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFieldSize.width.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 150.dp),
                    ) {

                        if (category.isNotEmpty()) {
                            items(
                                categories.filter {
                                    it.lowercase()
                                        .contains(category.lowercase())
                                }
                                    .sorted()
                            ) {
                                CategoryItems(title = it) { title ->
                                    counterVM.actionChangeSelectedCategory(title)
                                    expanded = false
                                }
                            }
                        } else {
                            items(
                                categories.sorted()
                            ) {
                                CategoryItems(title = it) { title ->
                                    counterVM.actionChangeSelectedCategory(title)
                                    expanded = false
                                    counterVM.loadLastEntryForGivenCategory()
                                }
                            }
                        }
                    }
                }
            }
        }

        TabRow(counterVM)

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            Arrangement.SpaceEvenly
        ) {

            Button(
                modifier = Modifier.width(160.dp),
                onClick = {
                    counterVM.saveEntry(category)
                    counterVM.actionChangeSelectedCategory("")
                    counterVM.actionPerPieceGramChange("")
                    counterVM.actionPerPieceAmountChange("")
                    counterVM.actionPerHundredChange("")
                    counterVM.actionPerHundredQuantityChange("")
                    expanded = false
                    keyboardController?.hide()
                },
            ) {
                Text(
                    text = stringResource(id = R.string.saveButton),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    //fontFamily = FontFamily.Monospace
                )
            }

        }

        val savedSugarCountGrouped by counterVM.savedEntriesToday.collectAsState()

        LazyColumn {
            items(
                savedSugarCountGrouped.toList()
                    .sortedByDescending { it.first.first }) { (key, value) ->
                ShowSugarCountItemsShared(key = key.second, valueList = value, true)
            }
        }
    }

    val alertDialog by counterVM.alertDialog.collectAsState()
    if (alertDialog) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.noGramValueInDatabaseYetTitle)) },
            onDismissRequest = { },
            confirmButton = {
                Button(

                    onClick = {
                        counterVM.actionDismissAlertDialog()
                    }) {
                    Text("Okay")
                }
            },
            text = {
                Text(stringResource(id = R.string.noGramValueInDatabaseYetDescription))
            }
        )
    }


    val barcodeNoEntry by counterVM.barcodeNoEntry.collectAsState()
    if (barcodeNoEntry) {
        DialogSingleButton(
            counterVM = counterVM,
            dialogTitle = "No database entry yet",
            dialogDescription = "This barcode doesn't have a databse entry yet. It will be added after saving the first entry.",
            buttonOnClick = { counterVM.actionDismissBarcodeNoEntryDialog() }
        )
    }


    val alertDialogGramThreshold by counterVM.alertDialogGramThreshold.collectAsState()
    if (alertDialogGramThreshold) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.alertGramThresholdTitle)) },
            onDismissRequest = { },
            confirmButton = {
                Button(
                    onClick = {
                        counterVM.actionGramThresholdDeleteLastEntry()
                    }) {
                    Text(stringResource(id = R.string.alertGramThresholdConfirmBtn))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        counterVM.actionGramThresholdKeepLastEntry()
                    }) {
                    Text(stringResource(id = R.string.alertGramThresholdDismissBtn))
                }
            },
            text = {
                Text(stringResource(id = R.string.alertGramThresholdDescription))
            }
        )
    }
}


@Composable
fun CategoryItems(
    title: String,
    onSelect: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(title)
            }
            .padding(10.dp)
    ) {
        Text(text = title, fontSize = 16.sp)
    }
}

@Composable
fun Barcode(counterVM: CounterVM) {
    Button(
        onClick = { counterVM.scanBarcode() }) {
        Text(text = "Barcode scannen")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    counterVM: CounterVM,
) {

    val dateOfEntryEpochSec by counterVM.dateOfEntryEpochSec.collectAsState()
    val datePickerShown by counterVM.datePickerShown.collectAsState()
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
    val nowMillis = System.currentTimeMillis()
    val xDaysAgoMillis = nowMillis - 2629743000

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                counterVM.actionChangeDatePickerVisibility(!datePickerShown)
            }) {
            Text(HelperMethods.formatDateToString(dateOfEntryEpochSec, "EEEE dd.MM.yy"))
        }
    }

    if (datePickerShown) {
        DatePickerDialog(
            onDismissRequest = {
                counterVM.actionChangeDatePickerVisibility(false)
            },
            confirmButton = {
                Button(onClick = {
                    counterVM.actionChangeDatePickerVisibility(false)
                    datePickerState.selectedDateMillis?.let {
                        counterVM.actionChangeDateOfEntryM3(
                            it / 1000
                        )
                    }
                }) {
                    Text(text = stringResource(id = R.string.saveButton))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        counterVM.actionChangeDatePickerVisibility(false)
                    }
                ) {
                    Text(text = stringResource(id = R.string.generalCancel))
                }
            }) {
            DatePicker(
                title = {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp,
                        text = stringResource(R.string.entryDateDescription)
                    )
                },
                headline = {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        fontSize = 18.sp,
                        text = stringResource(R.string.entryDateTitle)
                    )
                },
                state = datePickerState,
                dateValidator = { it in (xDaysAgoMillis + 1)..<nowMillis })
        }
    }
}
