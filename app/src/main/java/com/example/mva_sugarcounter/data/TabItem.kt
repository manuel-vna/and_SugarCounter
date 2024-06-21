package com.example.mva_sugarcounter.data

import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    val tabId: TabId,
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

enum class TabId {
    PerHundred,
    PerPiece
}