package com.jumparoundcreations.sugarcounter.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.data.AppLanguage
import de.sldw.composeonboarding.OnboardingPage

class OnboardingPage1(
    private val appLanguage: AppLanguage,
    private val fontColorOnBackground: Color
) : OnboardingPage() {


    @Composable
    override fun Content(paddingValues: PaddingValues) {

        //val context = LocalContext.current
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center

            ) {
                Text(
                    text = stringResource(R.string.settings_intoduction_counter_title),
                    fontSize = 20.sp,
                    color = fontColorOnBackground
                )
            }
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter =
                        when (appLanguage) {
                            AppLanguage.GERMAN -> {
                                painterResource(id = R.drawable.introduction_counter_top_de)
                            }

                            AppLanguage.SPANISH -> {
                                painterResource(id = R.drawable.introduction_counter_top_en)
                            }

                            else -> {
                                painterResource(id = R.drawable.introduction_counter_top_en)
                            }
                        },
                    contentDescription = stringResource(R.string.counterTitle)
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Justify,
                text = stringResource(R.string.settings_introduction_counter_top)
            )
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = when (appLanguage) {
                        AppLanguage.GERMAN -> {
                            painterResource(id = R.drawable.introduction_counter_middle_de)
                        }

                        AppLanguage.SPANISH -> {
                            painterResource(id = R.drawable.introduction_counter_middle_en)
                        }

                        else -> {
                            painterResource(id = R.drawable.introduction_counter_middle_en)
                        }
                    },
                    contentDescription = stringResource(R.string.counterTitle)
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Justify,
                text = stringResource(R.string.settings_introduction_counter_middle)
            )
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = when (appLanguage) {
                        AppLanguage.GERMAN -> {
                            painterResource(id = R.drawable.introduction_counter_bottom_de)
                        }

                        AppLanguage.SPANISH -> {
                            painterResource(id = R.drawable.introduction_counter_bottom_en)
                        }

                        else -> {
                            painterResource(id = R.drawable.introduction_counter_bottom_en)
                        }
                    },
                    contentDescription = stringResource(R.string.counterTitle)
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Justify,
                text = stringResource(R.string.settings_introduction_counter_bottom)
            )
        }
    }
}

class OnboardingPage2(
    private val appLanguage: AppLanguage,
    private val fontColorOnBackground: Color
) : OnboardingPage() {

    @Composable
    override fun Content(paddingValues: PaddingValues) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center

            ) {
                Text(
                    text = stringResource(R.string.settings_introduction_history_title),
                    fontSize = 20.sp,
                    color = fontColorOnBackground
                )
            }
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = when (appLanguage) {
                        AppLanguage.GERMAN -> {
                            painterResource(id = R.drawable.introduction_history_top_de)
                        }

                        AppLanguage.SPANISH -> {
                            painterResource(id = R.drawable.introduction_history_top_en)
                        }

                        else -> {
                            painterResource(id = R.drawable.introduction_history_top_en)
                        }
                    },
                    contentDescription = stringResource(R.string.counterTitle)
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Justify,
                text = stringResource(R.string.settings_introduction_history_top)
            )
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = when (appLanguage) {
                        AppLanguage.GERMAN -> {
                            painterResource(id = R.drawable.introduction_history_middle_de)
                        }

                        AppLanguage.SPANISH -> {
                            painterResource(id = R.drawable.introduction_history_middle_en)
                        }

                        else -> {
                            painterResource(id = R.drawable.introduction_history_middle_en)
                        }
                    },
                    contentDescription = stringResource(R.string.historyTitle)
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Justify,
                text = stringResource(R.string.settings_introduction_history_middle)
            )
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = when (appLanguage) {
                        AppLanguage.GERMAN -> {
                            painterResource(id = R.drawable.introduction_history_graph_de)
                        }

                        AppLanguage.SPANISH -> {
                            painterResource(id = R.drawable.introduction_history_graph_en)
                        }

                        else -> {
                            painterResource(id = R.drawable.introduction_history_graph_en)
                        }
                    },
                    contentDescription = stringResource(R.string.historyTitle)
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Justify,
                text = stringResource(R.string.settings_introduction_history_graph)
            )
        }
    }
}

class OnboardingPage3(
    private val appLanguage: AppLanguage,
    private val fontColorOnBackground: Color
) : OnboardingPage() {

    @Composable
    override fun Content(paddingValues: PaddingValues) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center

            ) {
                Text(
                    text = stringResource(R.string.settings_introduction_category_title),
                    fontSize = 20.sp,
                    color = fontColorOnBackground
                )
            }
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = when (appLanguage) {
                        AppLanguage.GERMAN -> {
                            painterResource(id = R.drawable.introduction_category_top_de)
                        }

                        AppLanguage.SPANISH -> {
                            painterResource(id = R.drawable.introduction_category_top_en)
                        }

                        else -> {
                            painterResource(id = R.drawable.introduction_category_top_en)
                        }
                    },
                    contentDescription = stringResource(R.string.categoryPlural)
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Justify,
                text = stringResource(R.string.settings_introduction_category_top)
            )
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = when (appLanguage) {
                        AppLanguage.GERMAN -> {
                            painterResource(id = R.drawable.introduction_category_bottom_de)
                        }

                        AppLanguage.SPANISH -> {
                            painterResource(id = R.drawable.introduction_category_bottom_en)
                        }

                        else -> {
                            painterResource(id = R.drawable.introduction_category_bottom_en)
                        }
                    },
                    contentDescription = stringResource(R.string.categoryPlural)
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Justify,
                text = stringResource(R.string.settings_introduction_category_bottom)
            )
        }
    }
}


class OnboardingPage4(
    private val appLanguage: AppLanguage,
    private val fontColorOnBackground: Color
) : OnboardingPage() {

    @Composable
    override fun Content(paddingValues: PaddingValues) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center

            ) {
                Text(
                    text = stringResource(R.string.settings_introduction_settings_title),
                    fontSize = 20.sp,
                    color = fontColorOnBackground
                )
            }
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        BorderStroke(
                            1.5.dp,
                            MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = when (appLanguage) {
                        AppLanguage.GERMAN -> {
                            painterResource(id = R.drawable.introduction_settings_top_de)
                        }

                        AppLanguage.SPANISH -> {
                            painterResource(id = R.drawable.introduction_settings_top_en)
                        }

                        else -> {
                            painterResource(id = R.drawable.introduction_settings_top_en)
                        }
                    },
                    contentDescription = stringResource(R.string.settingsTitle)
                )
            }
            Text(
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Justify,
                text = stringResource(R.string.settings_introduction_settings_top)
            )
            Spacer(modifier = Modifier.padding(vertical = 12.dp))
        }
    }
}
