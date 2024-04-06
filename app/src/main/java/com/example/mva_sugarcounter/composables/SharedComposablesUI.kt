package com.example.mva_sugarcounter.composables


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (backgroundColorPrimary) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
        )
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp),
        ) {

            Text(
                text = when (key) {
                    "TODAY" -> stringResource(R.string.timestampToday)
                    "YESTERDAY" -> stringResource(
                        R.string.timestampYesterday
                    )

                    else -> key
                },
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

        }

        valueList.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        counterVM.actionShowDeleteAlertDialog(it)
                    },
                horizontalArrangement = Arrangement.SpaceAround,
            )
            {
                Text(
                    text = it.amount.toString() + " x " + it.gramItem.toString() + "g",
                    modifier = Modifier
                        .padding(4.dp),
                )
                Text(
                    text = it.category,
                    modifier = Modifier
                        .padding(4.dp),
                )
                Text(
                    text = it.gramTotal.toString() + "g",
                    modifier = Modifier
                        .padding(4.dp),
                    textAlign = TextAlign.Right,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            val totalGramPerDayBlock = counterVM.calculateTotalGramPerDayBlock(valueList)

            Icon(
                painter = if (totalGramPerDayBlock <= 40) painterResource(id = R.drawable.baseline_check_circle_outline_24) else painterResource(
                    id = R.drawable.baseline_remove_circle_outline_24
                ),
                contentDescription = "",
            )

            Text(
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







