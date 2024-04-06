package com.example.mva_sugarcounter.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title:String, var icon: ImageVector, var screenRoute:String){
    object SugarCounter: BottomNavItem("Counter", Icons.Filled.FormatAlignJustify,"counter")
    object SugarHistory: BottomNavItem("History",Icons.Filled.AreaChart,"history")
    object Settings: BottomNavItem("Settings", Icons.Filled.Settings,"settings")
}