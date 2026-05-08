package com.jumparoundcreations.mva_sugarcounter.features.settingsFeature

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsRoute(
    context: Context,
    navController: NavController,
    viewModel: SettingsVM = koinViewModel(),
) {


}