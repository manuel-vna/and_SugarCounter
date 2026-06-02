package com.jumparoundcreations.mva_sugarcounter.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedTopAppBar(
    appBarTitle: String,
    onBackClickAction: () -> Unit,
    actionIconFirst: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 0.dp),
        title = { Text(appBarTitle) },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        actions = actionIconFirst,
        navigationIcon = {
            IconButton(onClick = { onBackClickAction.invoke() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
    )
}
