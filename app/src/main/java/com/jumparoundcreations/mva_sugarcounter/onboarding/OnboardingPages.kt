package com.jumparoundcreations.mva_sugarcounter.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.sldw.composeonboarding.OnboardingPage

class OnboardingPage1 : OnboardingPage() {
    @Composable
    override fun Content() {
        Text("Hello World")
    }
}

class OnboardingPage2 : OnboardingPage() {
    @Composable
    override fun Content() {
        Text("Hello Gelsenkirchen")
        canNavigateNext = true
    }
}
