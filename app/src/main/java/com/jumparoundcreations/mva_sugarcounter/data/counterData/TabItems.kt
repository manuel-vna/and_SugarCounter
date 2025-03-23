package com.jumparoundcreations.mva_sugarcounter.data.counterData

import androidx.compose.ui.graphics.vector.ImageVector

data class CounterTabItem(
    val gramCountMode: GramCountMode,
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
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