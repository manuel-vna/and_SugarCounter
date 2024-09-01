package com.jumparoundcreations.mva_sugarcounter.composables.onboardingUI

import androidx.compose.runtime.Composable
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage1
import com.jumparoundcreations.mva_sugarcounter.onboarding.OnboardingPage2
import de.sldw.composeonboarding.ComposeOnboarding
import de.sldw.composeonboarding.indicator.ProgressIndicator


@Composable
fun OnboardingUI() {
    ComposeOnboarding(
        pages = listOf(OnboardingPage1(), OnboardingPage2()),
        indicatorType = ProgressIndicator,
        onFinishPressed = { }
    )
}