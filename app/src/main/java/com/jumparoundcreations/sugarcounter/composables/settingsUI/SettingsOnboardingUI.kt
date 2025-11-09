package com.jumparoundcreations.sugarcounter.composables.settingsUI

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.NavController
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.composables.SharedTopAppBar
import com.jumparoundcreations.sugarcounter.composables.sharedUI.EmptyDataInfo
import com.jumparoundcreations.sugarcounter.data.AppLanguage
import com.jumparoundcreations.sugarcounter.onboarding.OnboardingPage1
import com.jumparoundcreations.sugarcounter.onboarding.OnboardingPage2
import com.jumparoundcreations.sugarcounter.onboarding.OnboardingPage3
import com.jumparoundcreations.sugarcounter.onboarding.OnboardingPage4
import de.sldw.composeonboarding.ComposeOnboarding
import de.sldw.composeonboarding.indicator.TextIndicator

@Composable
fun OnboardingUI(navController: NavController) {

    val systemLanguage = Locale.current.language
    val appLanguage = AppLanguage.fromCode(systemLanguage) ?: AppLanguage.ENGLISH

    var fontColorOnBackground: Color = Color.White
    val darkTheme: Boolean = isSystemInDarkTheme()
    if (darkTheme) {
        fontColorOnBackground = Color.Black
    }

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column {

        SharedTopAppBar(
            appBarTitle = stringResource(R.string.settings_introduction_title),
            onBackClickAction = {
                navController.popBackStack()
            }
        )

        if (isLandscape) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmptyDataInfo(stringResource(id = R.string.landscape_mode_no_graph_description))
            }
        } else {

            ComposeOnboarding(
                modifier = Modifier.consumeWindowInsets(WindowInsets.systemBars),
                pages = listOf(
                    OnboardingPage1(appLanguage, fontColorOnBackground),
                    OnboardingPage2(appLanguage, fontColorOnBackground),
                    OnboardingPage3(appLanguage, fontColorOnBackground),
                    OnboardingPage4(appLanguage, fontColorOnBackground)
                ),
                indicatorType = TextIndicator(),
                onFinishPressed = { navController.popBackStack() }
            )
        }

    }

}
