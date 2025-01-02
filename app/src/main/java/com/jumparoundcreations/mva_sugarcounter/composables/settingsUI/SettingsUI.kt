package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI


import android.content.Context
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
import androidx.navigation.NavController
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
            navController,
            stringResource(id = R.string.settings_button_faq_text),
            R.drawable.baseline_read_more_24,
        )

        SettingsButtonDonation(
            navController,
            stringResource(id = R.string.settings_button_donation_text),
            R.drawable.baseline_read_more_24,
        )


        SettingsButtonAbout(
            navController,
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
    navController: NavController,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp),
        onClick = {
            navController.navigate(NavItem.FAQ.screenRoute)
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
    navController: NavController,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp),
        onClick = {
            navController.navigate(NavItem.About.screenRoute)
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
fun SettingsButtonDonation(
    navController: NavController,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp),
        onClick = {

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
fun SettingsVersionCode() {
    Text(
        text = "SugarCounter " + BuildConfig.VERSION_NAME
    )
}



