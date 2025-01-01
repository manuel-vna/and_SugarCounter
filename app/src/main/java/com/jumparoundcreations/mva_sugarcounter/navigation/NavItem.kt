package com.jumparoundcreations.mva_sugarcounter.navigation

import com.jumparoundcreations.mva_sugarcounter.R

sealed class NavItem(var title: Int, var screenRoute: String) {

    data object FAQ :
        NavItem(R.string.settings_button_faq_text, "faq")

    data object About :
        NavItem(R.string.settings_button_about_text, "about")
}
