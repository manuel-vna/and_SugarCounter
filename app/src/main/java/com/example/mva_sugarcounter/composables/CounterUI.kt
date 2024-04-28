package com.example.mva_sugarcounter.composables


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
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mva_sugarcounter.R
import com.example.mva_sugarcounter.viewModels.CounterVM


@Composable
fun Counter(context: Context) {

    val counterVM: CounterVM = viewModel()
    val categories by counterVM.categories.collectAsState()

    var category by remember {
        mutableStateOf("")
    }
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


    // Category Field
    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()

            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = false
                }
            )
    ) {

        Text(
            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
            text = stringResource(R.string.foodType),
            fontSize = 16.sp,
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
                    if (it.count() <= 25) {
                        category = it
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
                                        .contains(category.lowercase()) || it.lowercase()
                                        .contains("others")
                                }
                                    .sorted()
                            ) {
                                CategoryItems(title = it) { title ->
                                    category = title
                                    expanded = false
                                }
                            }
                        } else {
                            items(
                                categories.sorted()
                            ) {
                                CategoryItems(title = it) { title ->
                                    category = title
                                    expanded = false
                                    counterVM.loadLastEntryForGivenCategory(category)
                                }
                            }
                        }
                    }
                }
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
                    text = stringResource(R.string.gramSugar),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                val gramValue by counterVM.gramValue.collectAsState()
                TextField(
                    value = gramValue,
                    onValueChange = {
                        if (it.isDigitsOnly()) counterVM.actionGramChange(it)
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

                val amountValue by counterVM.amountValue.collectAsState()
                Text(
                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                    text = stringResource(R.string.amountSugar),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                TextField(
                    value = amountValue,
                    onValueChange = { if (it.isDigitsOnly()) counterVM.actionAmountChange(it) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { counterVM.actionAmountChange("") }) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "arrow",
                            )
                        }
                    }
                )

            }
        }

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
                    category = ""
                    counterVM.actionGramChange("")
                    counterVM.actionAmountChange("")
                    expanded = false
                },
            ) {
                Text(text = stringResource(id = R.string.saveButton))
            }

        }

        val savedSugarCountGrouped by counterVM.savedEntriesNowMinus1Day.collectAsState()

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




