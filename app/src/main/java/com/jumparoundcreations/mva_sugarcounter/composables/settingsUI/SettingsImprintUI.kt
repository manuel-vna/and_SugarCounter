package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import org.koin.compose.getKoin
import org.koin.core.qualifier.named

@Composable
fun ImprintUI() {

    val termsHtml: String = getKoin().get(named("imprint"))

    Column(modifier = Modifier.padding(16.dp)) {
        val spannedText = HtmlCompat.fromHtml(termsHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
        Text(
            text = spannedText.toString(), // Use AnnotatedString if rich formatting is required
            style = MaterialTheme.typography.bodyLarge,
        )
    }

}

