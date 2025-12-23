package com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.ui.components.SharedTopAppBar
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

        WebView(
            state = rememberWebViewStateWithHTMLData(
                termsHtml.replace(
                    "##STYLE_PLACEHOLDER##",
                    """<style>
                        body {
                            background-color: rgb(
                                ${MaterialTheme.colorScheme.background.red * 255},
                                ${MaterialTheme.colorScheme.background.green * 255},
                                ${MaterialTheme.colorScheme.background.blue * 255}
                            ); 
                            color: rgb(
                                ${MaterialTheme.colorScheme.onBackground.red * 255},
                                ${MaterialTheme.colorScheme.onBackground.green * 255},
                                ${MaterialTheme.colorScheme.onBackground.blue * 255}
                            );
                        }
                    </style>""".trimMargin()
                )
            ),
            modifier = Modifier.fillMaxSize()
        )
    }

}

