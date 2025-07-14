package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.SharedTopAppBar
import org.koin.compose.getKoin
import org.koin.core.qualifier.named

@Composable
fun PrivacyPolicyUI(navController: NavController) {

    val termsHtml: String = getKoin().get(named("privacyPolicy"))

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        SharedTopAppBar(
            appBarTitle = stringResource(R.string.about_title_privacy_policy),
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