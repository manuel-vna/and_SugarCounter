package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.SharedTopAppBar
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage1
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage2
import de.sldw.composeonboarding.ComposeOnboarding
import de.sldw.composeonboarding.indicator.ProgressIndicator

@Composable
fun OnboardingUI(navController: NavController) {

    Column {

        SharedTopAppBar(
            appBarTitle = stringResource(R.string.settings_introduction_title),
            onBackClickAction = {
                navController.popBackStack()
            }
        )

        ComposeOnboarding(
            pages = listOf(OnboardingPage1(navController), OnboardingPage2()),
            indicatorType = ProgressIndicator,
            onFinishPressed = { navController.popBackStack() }
        )
    }

}
