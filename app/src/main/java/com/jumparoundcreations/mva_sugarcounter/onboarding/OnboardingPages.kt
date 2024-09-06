package com.jumparoundcreations.mva_sugarcounter.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.sldw.composeonboarding.OnboardingPage
import de.sldw.composeonboarding.PolicyOnboardingPage

class OnboardingPage1 : OnboardingPage() {
    @Composable
    override fun Content() {
        Text("Hello World")
    }
}

class OnboardingPage2 : PolicyOnboardingPage() {
    @Composable
    override fun Content() {
        Text("Hello Gelsenkirchen")
    }
}
