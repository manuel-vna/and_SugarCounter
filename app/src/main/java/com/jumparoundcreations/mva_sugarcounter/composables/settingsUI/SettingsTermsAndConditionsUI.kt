package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
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

        val spannedText = HtmlCompat.fromHtml(termsHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
        Text(
            text = spannedText.toString(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }

}