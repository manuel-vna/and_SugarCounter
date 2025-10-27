package com.jumparoundcreations.sugarcounter.composables.sharedUI

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BlurOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyDataInfo(description: String) {
    Icon(
        modifier = Modifier.size(64.dp),
        imageVector = Icons.Rounded.BlurOff,
        contentDescription = "arrow",
    )
    Text(text = description)
}