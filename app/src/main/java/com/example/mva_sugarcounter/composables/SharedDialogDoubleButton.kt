package com.example.mva_sugarcounter.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DialogDoubleButton(
    dialogTitle: String,
    dialogDescription: String,
    buttonOnConfirmText: String,
    buttonOnDismissText: String,
    buttonOnConfirm: () -> Unit,
    buttonOnDismiss: () -> Unit
) {
    AlertDialog(
        title = { Text(text = dialogTitle) },
        text = {
            Text(dialogDescription)
        },
        confirmButton = {
            Button(
                onClick = {
                    buttonOnConfirm.invoke()
                }) {
                Text(buttonOnConfirmText)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    buttonOnDismiss.invoke()
                }) {
                Text(buttonOnDismissText)
            }
        },
        onDismissRequest = { },
    )
}