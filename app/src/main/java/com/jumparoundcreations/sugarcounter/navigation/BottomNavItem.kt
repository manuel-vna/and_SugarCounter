package com.jumparoundcreations.sugarcounter.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.jumparoundcreations.sugarcounter.R

sealed class BottomNavItem(var title: Int, var icon: ImageVector, var screenRoute: String) {
    data object SugarCounter :
        BottomNavItem(R.string.counterTitle, Icons.Filled.FormatAlignJustify, "counter")

    data object SugarHistory :
        BottomNavItem(R.string.historyTitle, Icons.Filled.AreaChart, "history")

    data object CategoryTitle :
        BottomNavItem(R.string.categoryPlural, Icons.Filled.Category, "category")

    data object Settings : BottomNavItem(R.string.settingsTitle, Icons.Filled.Settings, "settings")
}