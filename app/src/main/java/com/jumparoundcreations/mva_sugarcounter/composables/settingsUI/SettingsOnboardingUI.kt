package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.SharedTopAppBar
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage1
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage2
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage3
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage4
import de.sldw.composeonboarding.ComposeOnboarding
import de.sldw.composeonboarding.indicator.ProgressIndicator

@Composable
fun OnboardingUI(navController: NavController) {

    val systemLanguage = Locale.current.language

    var fontColorOnBackground: Color = Color.White
    val darkTheme: Boolean = isSystemInDarkTheme()
    if (darkTheme) {
        fontColorOnBackground = Color.Black
    }

    Column {

        SharedTopAppBar(
            appBarTitle = stringResource(R.string.settings_introduction_title),
            onBackClickAction = {
                navController.popBackStack()
            }
        )

        ComposeOnboarding(
            pages = listOf(
                OnboardingPage1(systemLanguage, fontColorOnBackground),
                OnboardingPage2(systemLanguage, fontColorOnBackground),
                OnboardingPage3(systemLanguage, fontColorOnBackground),
                OnboardingPage4(systemLanguage, fontColorOnBackground)
            ),
            indicatorType = ProgressIndicator,
            onFinishPressed = { navController.popBackStack() }
        )
    }

}
