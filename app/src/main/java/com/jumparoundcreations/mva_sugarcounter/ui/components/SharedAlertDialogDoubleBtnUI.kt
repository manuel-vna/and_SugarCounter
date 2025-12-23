package com.jumparoundcreations.mva_sugarcounter.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun ShowAlertDialogDoubleBtn(
    dialogTitle: String,
    dialogDescription: String,
    confirmBtnText: String,
    confirmBtnAction: () -> Unit,
    dismissBtnText: String,
    dismissBtnAction: () -> Unit,
    onDismissRequest: () -> Unit
) {
    /**
     * General method that shows an alert dialog wit two buttons: Confirm and Dismiss
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
        dismissButton = {
            OutlinedButton(onClick = { dismissBtnAction.invoke() }) {
                Text(dismissBtnText)
            }

        },
        text = {
            Text(dialogDescription)
        })
}