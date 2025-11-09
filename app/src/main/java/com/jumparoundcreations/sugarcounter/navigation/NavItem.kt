package com.jumparoundcreations.sugarcounter.navigation

import com.jumparoundcreations.sugarcounter.R

sealed class NavItem(var title: Int, var screenRoute: String) {

    data object FAQ :
        NavItem(R.string.settings_button_faq_text, "faq")

    data object ThirdPartyLibraries :
        NavItem(R.string.settings_third_party_licenses_text, "thirdPartyLibraries")

    data object TermsAndConditions :
        NavItem(R.string.about_title_terms_and_conditions, "termsAndConditions")

    data object PrivacyPolicy :
        NavItem(R.string.about_title_privacy_policy, "privacyPolicy")

    data object Imprint :
        NavItem(R.string.about_title_imprint, "imprint")

    data object Onboarding :
        NavItem(R.string.settings_introduction_title, "onboarding")

}
