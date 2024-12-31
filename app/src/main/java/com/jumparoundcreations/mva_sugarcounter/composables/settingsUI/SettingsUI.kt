package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.jumparoundcreations.mva_sugarcounter.BuildConfig
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named


@Composable
fun Settings(context: Context) {

    //get an instance of the ViewModel
    val settingsVM: SettingsVM = koinViewModel()

    //collecting states
    val settingsScreenShown by settingsVM.settingsScreenShown.collectAsState()
    val faqScreenShown by settingsVM.faqScreenShown.collectAsState()

    settingsVM.actionResetThresholdSliderValuesToSharedPref()

    if (settingsScreenShown) {
        SettingsScreen(context, settingsVM)
    }

    if (faqScreenShown) {
        FAQScreen(context)
    }

}

@Composable
fun SettingsScreen(
    context: Context,
    settingsVM: SettingsVM,
    sharedPrefsMain: SharedPreferences = koinInject(qualifier = named("sharedPrefsMain"))
) {
    Column(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        SettingsActivateCaloriesCounter(settingsVM, sharedPrefsMain)

        SettingsSharedSliderThreshold(
            settingsVM = settingsVM,
        )

        HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 32.dp))

        SettingsButtonExportEntries(
            context = context,
            settingsVM = settingsVM,
            descriptionText = stringResource(R.string.export_settings_button),
            buttonIcon = R.drawable.baseline_read_more_24
        )

        SettingsButtonFAQs(
            context,
            settingsVM,
            stringResource(id = R.string.settings_button_faq_text),
            R.drawable.baseline_read_more_24,
        )

        SettingsButtonThirdPartyLicenses(
            context,
            stringResource(id = R.string.settings_third_party_licenses_text),
            R.drawable.baseline_read_more_24,
        )

        SettingsButtonAbout(
            context,
            settingsVM,
            stringResource(id = R.string.settings_button_about_text),
            R.drawable.baseline_read_more_24,
        )

        SettingsVersionCode()

        ExportProgressIndicator(settingsVM = settingsVM)

        ExportBottomSheet(context)
    }
}

@Composable
fun SettingsActivateCaloriesCounter(
    settingsVM: SettingsVM,
    sharedPrefsMain: SharedPreferences
) {

    // START: get status of switch in case the app was closed in the meantime and set the state of the switch accordingly
    val caloriesCounterSwitchActivated = sharedPrefsMain.getBoolean(
        "caloriesCounterActivated",
        false
    )
    settingsVM.actionChangeCaloriesCounterSwitch(caloriesCounterSwitchActivated)
    // END: get status of switch in case the app was closed in the meantime and set the state of the switch accordingly


    val caloriesCounterActivated by settingsVM.caloriesCounterActivated.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.calories_activation_switch_title)
        )
        Switch(
            checked = caloriesCounterActivated,
            onCheckedChange = { settingsVM.actionChangeCaloriesCounterGeneral(it) }
        )
    }

}

@Composable
fun SettingsButtonFAQs(
    context: Context,
    settingsVM: SettingsVM,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp),
        onClick = {
            settingsVM.actionChangeSettingsScreenVisibility(isShown = false)
            settingsVM.actionChangeFaqScreenVisibility(isShown = true)
        }) {
        Text(
            text = "$descriptionText   "
        )
        Icon(
            painter = painterResource(id = buttonIcon),
            contentDescription = "",
        )
    }
}

@Composable
fun SettingsButtonAbout(
    context: Context,
    settingsVM: SettingsVM,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp),
        onClick = {
            //TBD navigation to SettingsAboutUI.kt

        }
    ) {
        Text(
            text = "$descriptionText   "
        )
        Icon(
            painter = painterResource(id = buttonIcon),
            contentDescription = "",
        )
    }
}

@Composable
fun SettingsButtonThirdPartyLicenses(
    context: Context,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 16.dp),
        onClick = {
            val intent = Intent(context, OssLicensesMenuActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

        }) {
        Text(
            text = "$descriptionText   "
        )
        Icon(
            painter = painterResource(id = buttonIcon),
            contentDescription = "",
        )

    }
}


@Composable
fun SettingsVersionCode() {
    Text(
        text = "SugarCounter " + BuildConfig.VERSION_NAME
    )
}



