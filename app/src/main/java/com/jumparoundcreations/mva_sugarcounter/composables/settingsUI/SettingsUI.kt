package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.jumparoundcreations.mva_sugarcounter.BuildConfig
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.navigation.NavItem
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named


@Composable
fun Settings(
    context: Context,
    navController: NavController,
    sharedPrefsMain: SharedPreferences = koinInject(qualifier = named("sharedPrefsMain"))
) {

    val settingsVM: SettingsVM = koinViewModel()
    settingsVM.actionResetThresholdSliderValuesToSharedPref()


    LazyColumn(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp),
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
                title = "Funktionen",
                sectionRows = listOf(
                    Pair(
                        stringResource(R.string.export_settings_button),
                        { settingsVM.actionChangeDataPreExportBottomSheetShown(true) }),
                    Pair("Automatische Löschung",
                        { settingsVM.actionChangeEntriesDeletionBottomSheetShown(true) })
                )
            )

            SettingsEntriesDeletion(settingsVM = settingsVM)

            ExportProgressIndicator(settingsVM = settingsVM)

            SettingsPostExportBottomSheet(context)
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            SettingsSectionBoxUI(
                title = "Hilfe",
                sectionRows = listOf(
                    Pair("FAQs", { navController.navigate(NavItem.FAQ.screenRoute) }),
                    Pair("Introduction", { println("Onboarding field clicked") }),
                    Pair("Donation", { println("Donation field clicked") })
                )
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            SettingsSectionBoxUI(
                title = "Über die App",
                sectionRows = listOf(
                    Pair(
                        "Terms and Conditions",
                        { navController.navigate(NavItem.TermsAndConditions.screenRoute) }),
                    Pair(
                        "Privacy Policy",
                        { navController.navigate(NavItem.PrivacyPolicy.screenRoute) }),
                    Pair("Third Party Licenses", {
                        val intent = Intent(context, OssLicensesMenuActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }),
                    Pair("Imprint", { navController.navigate(NavItem.Imprint.screenRoute) })
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



