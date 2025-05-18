package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.SharedTopAppBar
import com.jumparoundcreations.mva_sugarcounter.data.AppLanguage
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage1
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage2
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage3
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage4
import de.sldw.composeonboarding.ComposeOnboarding
import de.sldw.composeonboarding.indicator.ProgressIndicator

@Composable
fun OnboardingUI(navController: NavController) {

    val systemLanguage = Locale.current.language
    val appLanguage = AppLanguage.fromCode(systemLanguage) ?: AppLanguage.ENGLISH

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
            modifier = Modifier.consumeWindowInsets(WindowInsets.systemBars),
            pages = listOf(
                OnboardingPage1(appLanguage, fontColorOnBackground),
                OnboardingPage2(appLanguage, fontColorOnBackground),
                OnboardingPage3(appLanguage, fontColorOnBackground),
                OnboardingPage4(appLanguage, fontColorOnBackground)
            ),
            indicatorType = ProgressIndicator,
            onFinishPressed = { navController.popBackStack() }
        )
    }

}
