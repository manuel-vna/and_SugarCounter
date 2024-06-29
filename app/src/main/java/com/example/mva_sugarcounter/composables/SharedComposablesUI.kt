package com.example.mva_sugarcounter.composables


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mva_sugarcounter.R
import com.example.mva_sugarcounter.data.Entry
import com.example.mva_sugarcounter.viewModels.CounterVM

@Composable
fun ShowSugarCountItemsShared(
    key: String,
    valueList: List<Entry>,
    backgroundColorPrimary: Boolean
) {

    val counterVM: CounterVM = viewModel()
    val totalGramPerDayBlock = counterVM.calculateTotalGramPerDayBlock(valueList)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp),
        border = if (totalGramPerDayBlock > 45) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
        } else if (backgroundColorPrimary) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        } else {
            null
        },
        colors = CardDefaults.cardColors(
            containerColor = if (backgroundColorPrimary) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondaryContainer,
        )
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp),
        ) {

            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = when (key) {
                    "TODAY" -> stringResource(R.string.timestampToday)
                    "YESTERDAY" -> stringResource(
                        R.string.timestampYesterday
                    )

                    else -> key
                },
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic
            )

        }

        valueList.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        counterVM.actionShowDeleteAlertDialog(it)
                    },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier.weight(3f),
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        textAlign = TextAlign.Start,
                        text = it.perPieceAmount.toString() + " x " + if (it.perPieceGram.toString().length == 1) {
                            "  "
                        } else {
                            ""
                        } + it.perPieceGram.toString() + "g",
                        modifier = Modifier
                            .padding(start = 8.dp),
                    )
                }
                Box(
                    modifier = Modifier.weight(8f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = it.category,
                        modifier = Modifier
                            .padding(horizontal = 2.dp),
                    )
                }
                Box(
                    modifier = Modifier.weight(2f),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Text(
                        textAlign = TextAlign.End,
                        text = it.gramTotal.toString() + "g",
                        modifier = Modifier
                            .padding(end = 8.dp),
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .padding(top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                painter = if (totalGramPerDayBlock <= 45) painterResource(id = R.drawable.baseline_check_circle_outline_24) else painterResource(
                    id = R.drawable.baseline_remove_circle_outline_24
                ),
                tint = if (totalGramPerDayBlock > 45) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                contentDescription = "",
            )

            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = stringResource(id = R.string.totalAmountSugar) + ": " + counterVM.calculateTotalGramPerDayBlock(
                    valueList
                ) + "g ",
                fontWeight = FontWeight.Bold
            )
        }

    }


    val categoryItemDeleteDialog by counterVM.categoryItemDeleteDialog.collectAsState()
    val categoryItemDeleteObject by counterVM.categoryItemDeleteObject.collectAsState()
    if (categoryItemDeleteDialog) {
        AlertDialog(
            title = { Text(text = "Delete this item?") },
            onDismissRequest = { },
            confirmButton = {
                Button(
                    onClick = {
                        Log.d("Tag", "Delete item: " + categoryItemDeleteObject.category)
                        counterVM.actionDeleteSpecificEntryRow(categoryItemDeleteObject.id)
                        counterVM.actionDismissDeleteAlertDialog()
                    }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        Log.d("Tag", "Cancel item: " + categoryItemDeleteObject.category)
                        counterVM.actionDismissDeleteAlertDialog()
                    }) {
                    Text("Cancel")
                }

            },
            text = {
                Text(categoryItemDeleteObject.gramTotal.toString() + "g " + categoryItemDeleteObject.category)
            }
        )
    }

}







