package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.SharedTopAppBar
import org.koin.compose.getKoin
import org.koin.core.qualifier.named

@Composable
fun ImprintUI(navController: NavController) {

    val termsHtml: String = getKoin().get(named("imprint"))

    Column {

        SharedTopAppBar(
            appBarTitle = stringResource(R.string.about_title_imprint),
            onBackClickAction = {
                navController.popBackStack()
            }
        )

        val spannedText = HtmlCompat.fromHtml(termsHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
        Text(
            text = spannedText.toString(), // Use AnnotatedString if rich formatting is required
            style = MaterialTheme.typography.bodyLarge,
        )
    }

}

