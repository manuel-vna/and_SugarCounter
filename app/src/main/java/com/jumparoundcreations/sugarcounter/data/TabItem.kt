package com.jumparoundcreations.sugarcounter.data

import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    val gramCountMode: GramCountMode,
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

enum class GramCountMode {
    PerHundred,
    PerPiece
}