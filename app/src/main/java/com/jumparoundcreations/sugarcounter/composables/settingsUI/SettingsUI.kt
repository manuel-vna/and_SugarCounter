package com.jumparoundcreations.sugarcounter.composables.settingsUI


import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jumparoundcreations.sugarcounter.BuildConfig
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.data.settingsData.BottomSheetsSettings
import com.jumparoundcreations.sugarcounter.navigation.NavItem
import com.jumparoundcreations.sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel


@Composable
fun Settings(
    context: Context,
    navController: NavController,
) {

    val settingsVM: SettingsVM = koinViewModel()
    settingsVM.actionResetThresholdSliderValuesToSharedPref()


    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
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
                sectionRows =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // S = Android 12
                        listOf(
                            Pair(
                                stringResource(R.string.export_settings_button),
                                { settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.DATA_PRE_EXPORT) }),
                            Pair(
                                stringResource(R.string.settings_entries_deletion_bottom_sheet_description_switch),
                                { settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.ENTRIES_DELETION) }),
                            Pair(
                                stringResource(R.string.settings_color_scheme_title),
                                { settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.COLOR_SCHEME) })

                        )
                    } else {
                        listOf(
                            Pair(
                                stringResource(R.string.export_settings_button),
                                { settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.DATA_PRE_EXPORT) }),
                            Pair(
                                stringResource(R.string.settings_entries_deletion_bottom_sheet_description_switch),
                                { settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.ENTRIES_DELETION) }),
                        )
                    }
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
                    )
                )
            )

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
                            navController.navigate(NavItem.ThirdPartyLibraries.screenRoute)
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



