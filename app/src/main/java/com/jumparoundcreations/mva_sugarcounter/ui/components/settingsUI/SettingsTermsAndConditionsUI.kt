package com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.ui.components.SharedTopAppBar
import com.jumparoundcreations.mva_sugarcounter.util.NumberConstants
import org.koin.compose.getKoin
import org.koin.core.qualifier.named

@Composable
fun TermsAndConditionsUI(navController: NavController) {

    val termsHtml: String = getKoin().get(named("termsAndConditions"))

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        SharedTopAppBar(
            appBarTitle = stringResource(R.string.about_title_terms_and_conditions),
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
                                ${
                        MaterialTheme.colorScheme.background.red *
                                NumberConstants.COLOR_RGB_MULTIPLIER
                    },
                                ${
                        MaterialTheme.colorScheme.background.green *
                                NumberConstants.COLOR_RGB_MULTIPLIER
                    },
                                ${
                        MaterialTheme.colorScheme.background.blue *
                                NumberConstants.COLOR_RGB_MULTIPLIER
                    }
                            ); 
                            color: rgb(
                                ${
                        MaterialTheme.colorScheme.onBackground.red *
                                NumberConstants.COLOR_RGB_MULTIPLIER
                    },
                                ${
                        MaterialTheme.colorScheme.onBackground.green *
                                NumberConstants.COLOR_RGB_MULTIPLIER
                    },
                                ${
                        MaterialTheme.colorScheme.onBackground.blue *
                                NumberConstants.COLOR_RGB_MULTIPLIER
                    }
                            );
                        }
                    </style>""".trimMargin()
                )
            ),
            modifier = Modifier.fillMaxSize()
        )

    }

}