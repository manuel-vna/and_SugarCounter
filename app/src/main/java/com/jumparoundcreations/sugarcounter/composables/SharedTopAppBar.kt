package com.jumparoundcreations.sugarcounter.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedToppAppBar(
    appBarTitle: String,
    onBackClickAction: () -> Unit,
    actionIconFirst: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(appBarTitle) },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        actions = actionIconFirst,
        navigationIcon = {
            IconButton(onClick = { onBackClickAction.invoke() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        })
}