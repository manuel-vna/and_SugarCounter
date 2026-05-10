package com.jumparoundcreations.mva_sugarcounter.features.settingsFeature

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI.SettingsUI

@Composable
fun SettingsScreen(
    context: Context,
    navController: NavController,
    settingsStates: SettingsStates
) {

    when {
        settingsStates.isLoading -> {
            print("Settings: isLoading")
        }
        settingsStates.isError -> {
        print("Settings: isError")
        }
        else -> {
            SettingsUI(
                context,
                navController
            )
        }
    }

}