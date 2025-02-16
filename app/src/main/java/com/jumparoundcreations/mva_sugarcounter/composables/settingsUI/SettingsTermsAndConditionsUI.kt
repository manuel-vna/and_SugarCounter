package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.SharedTopAppBar
import org.koin.compose.getKoin
import org.koin.core.qualifier.named

@Composable
fun TermsAndConditionsUI(navController: NavController) {

    val termsHtml: String = getKoin().get(named("termsAndConditions"))

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {

        SharedTopAppBar(
            appBarTitle = stringResource(R.string.about_title_terms_and_conditions),
            onBackClickAction = {
                navController.popBackStack()
            }
        )

        WebView(
            state = rememberWebViewStateWithHTMLData(termsHtml),
            modifier = Modifier.fillMaxSize()
        )

    }

}