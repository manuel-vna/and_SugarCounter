package com.jumparoundcreations.sugarcounter.data.counterData

import androidx.compose.ui.graphics.vector.ImageVector

data class CounterTabItem(
    val gramCountMode: GramCountMode,
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val contentDescription: String
)

enum class GramCountMode {
    PerHundred,
    PerPiece
}

data class HistoryTabItem(
    val index: Int,
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)