package com.jumparoundcreations.mva_sugarcounter.composables.counterUI

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM

@Composable
fun DialogSingleButton(
    counterVM: CounterVM,
    dialogTitle: String,
    dialogDescription: String,
    buttonOnClick: () -> Unit
) {
    AlertDialog(
        title = { Text(text = dialogTitle) },
        onDismissRequest = { },
        confirmButton = {
            Button(

                onClick = {
                    buttonOnClick.invoke()
                }) {
                Text("Okay")
            }
        },
        text = {
            Text(dialogDescription)
        }
    )
}