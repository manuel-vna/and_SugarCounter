package com.jumparoundcreations.sugarcounter.ui.components.settingsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.ui.components.SharedTopAppBar
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer


@Composable
fun SettingsThirdPartyLibrariesUI(navController: NavController) {

    val libraries by produceLibraries()

    Column {
        SharedTopAppBar(
            appBarTitle = stringResource(R.string.settings_third_party_licenses_text),
            onBackClickAction = {
                navController.popBackStack()
            }
        )

        LibrariesContainer(
            libraries = libraries,
            modifier = Modifier.fillMaxSize()
        )
    }
    
}