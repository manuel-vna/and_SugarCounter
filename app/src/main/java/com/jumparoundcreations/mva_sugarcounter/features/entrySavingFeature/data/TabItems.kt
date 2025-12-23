package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data

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