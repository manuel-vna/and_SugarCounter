package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.jumparoundcreations.mva_sugarcounter.BuildConfig
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.navigation.NavItem
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun Settings(
    context: Context,
    navController: NavController,
    sharedPrefsMain: SharedPreferences = koinInject()
) {

    val settingsVM: SettingsVM = koinViewModel()
    settingsVM.actionResetThresholdSliderValuesToSharedPref()


    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        item { SettingsActivateCaloriesCounter(settingsVM) }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { SettingsSharedSliderThreshold(settingsVM = settingsVM) }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsPostExportBottomSheet(
                context = context,
                settingsVM = settingsVM,
                descriptionText = stringResource(R.string.export_settings_button),
                buttonIcon = R.drawable.baseline_read_more_24
            )

            SettingsSectionBoxUI(
                title = stringResource(R.string.settings_functions_other_title),
                sectionRows = listOf(
                    Pair(
                        stringResource(R.string.export_settings_button),
                        { settingsVM.actionChangeDataPreExportBottomSheetShown(true) }),
                    Pair(
                        stringResource(R.string.settings_entries_deletion_bottom_sheet_description_switch),
                        { settingsVM.actionChangeEntriesDeletionBottomSheetShown(true) }),
                    Pair(
                        stringResource(R.string.settings_color_scheme_title),
                        { settingsVM.actionChangeColorSchemeBottomSheetShown(true) })
                )
            )

            SettingsEntriesDeletion(settingsVM = settingsVM)

            SettingsColorScheme(settingsVM = settingsVM)

            ExportProgressIndicator(settingsVM = settingsVM)

            SettingsPostExportBottomSheet(context)
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            SettingsSectionBoxUI(
                title = stringResource(R.string.general_help),
                sectionRows = listOf(
                    Pair(
                        stringResource(R.string.settings_button_faq_text),
                        { navController.navigate(NavItem.FAQ.screenRoute) }
                    ),
                    Pair(
                        stringResource(R.string.settings_introduction_title),
                        { navController.navigate(NavItem.Onboarding.screenRoute) }
                    ),
                    Pair(
                        stringResource(R.string.settings_donation_title),
                        { settingsVM.actionChangeDonationBottomSheetShown(true) }
                    )
                )
            )

            SettingsDonationUI(context, settingsVM)
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            SettingsSectionBoxUI(
                title = stringResource(R.string.settings_button_about_text),
                sectionRows = listOf(
                    Pair(
                        stringResource(R.string.about_title_terms_and_conditions),
                        { navController.navigate(NavItem.TermsAndConditions.screenRoute) }),
                    Pair(
                        stringResource(R.string.about_title_privacy_policy),
                        { navController.navigate(NavItem.PrivacyPolicy.screenRoute) }),
                    Pair(
                        stringResource(R.string.settings_third_party_licenses_text),
                        {
                            val intent = Intent(context, OssLicensesMenuActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }),
                    Pair(
                        stringResource(R.string.about_title_imprint),
                        { navController.navigate(NavItem.Imprint.screenRoute) })
                )
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item { SettingsVersionCode() }

    }
}

@Composable
fun SettingsVersionCode() {

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.app_name) + " " + BuildConfig.VERSION_NAME,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = stringResource(R.string.about_concluding_part)
    )
}



