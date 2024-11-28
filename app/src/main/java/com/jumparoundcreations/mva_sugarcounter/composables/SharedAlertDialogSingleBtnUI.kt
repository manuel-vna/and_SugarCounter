package com.jumparoundcreations.mva_sugarcounter.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ShowAlertDialogSingleBtn(
    dialogTitle: String,
    dialogDescription: String,
    confirmBtnText: String,
    confirmBtnAction: () -> Unit,
    onDismissRequest: () -> Unit
) {
    /**
     * General method that shows an alert dialog wit one button: Confirm
     * @return Unit
     */
    AlertDialog(
        title = { Text(text = dialogTitle) },
        onDismissRequest = { onDismissRequest.invoke() },
        confirmButton = {
            Button(onClick = { confirmBtnAction.invoke() }) {
                Text(confirmBtnText)
            }
        },
        text = {
            Text(dialogDescription)
        })
}