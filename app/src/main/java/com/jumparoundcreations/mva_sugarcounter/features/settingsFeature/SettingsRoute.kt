package com.jumparoundcreations.mva_sugarcounter.features.settingsFeature

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsRoute(
    context: Context,
    navController: NavController,
    viewModel: SettingsVM = koinViewModel(),
    snackbarHostState: SnackbarHostState,
) {
    val settingsStates by viewModel.settingsStates.collectAsStateWithLifecycle()
    var pendingSnackbar by remember { mutableStateOf<SettingsSnackbarMessage?>(null) }

    SettingsScreen(
        context,
        navController,
        settingsStates,
    )

    LaunchedEffect(Unit) {
        viewModel.settingsEffects.collect { effect ->
            when (effect) {
                is SettingsEffect.ShowSnackbar -> {
                    pendingSnackbar = effect.message
                }
            }
        }
    }

    // Show snackbar when pending (stringResource must be called in Composable context)
    val msg = pendingSnackbar
    val snackbarText = if (msg != null) discoverSnackbarText(msg) else null

    LaunchedEffect(snackbarText) {
        snackbarText?.let { text ->
            snackbarHostState.showSnackbar(text)
            pendingSnackbar = null
        }
    }
}

@Composable
private fun discoverSnackbarText(msg: SettingsSnackbarMessage): String =
    when (msg) {
        is SettingsSnackbarMessage.GramThresholdChangeSuccess ->
            stringResource(
                R.string.snackbar_threshold_change_success,
                msg.newThreshold,
            )
        is SettingsSnackbarMessage.GramThresholdChangeCanceled ->
            stringResource(
                R.string.snackbar_threshold_change_cancel,
            )
    }
