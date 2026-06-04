package com.jumparoundcreations.mva_sugarcounter.features.settingsFeature

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI.SettingsUI

@Composable
fun SettingsScreen(
    context: Context,
    navController: NavController,
    settingsStates: SettingsStates,
) {
    when {
        settingsStates.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        settingsStates.isError -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.general_error))
            }
        }
        else -> {
            SettingsUI(
                context,
                navController,
            )
        }
    }
}
