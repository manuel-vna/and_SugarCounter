package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI


import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.jumparoundcreations.mva_sugarcounter.BuildConfig
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.navigation.NavItem
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel


@Composable
fun SettingsAboutUI(navController: NavController) {

    val context = LocalContext.current
    val settingsVM: SettingsVM = koinViewModel()

    Column(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {


        SettingsButtonTermsAndConditions(
            navController = navController,
            descriptionText = stringResource(R.string.about_title_terms_and_conditions),
            buttonIcon = R.drawable.baseline_read_more_24
        )

        SettingsButtonPrivacyPolicy(
            navController = navController,
            descriptionText = stringResource(R.string.about_title_privacy_policy),
            buttonIcon = R.drawable.baseline_read_more_24
        )

        SettingsButtonThirdPartyLicenses(
            context,
            stringResource(id = R.string.settings_third_party_licenses_text),
            R.drawable.baseline_read_more_24,
        )

        SettingsButtonImprint(
            navController = navController,
            descriptionText = stringResource(R.string.about_title_imprint),
            buttonIcon = R.drawable.baseline_read_more_24
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.about_concluding_part, BuildConfig.VERSION_NAME)
        )

    }

}

@Composable
fun SettingsButtonTermsAndConditions(
    navController: NavController,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp),
        onClick = {
            navController.navigate(NavItem.TermsAndConditions.screenRoute)
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
fun SettingsButtonPrivacyPolicy(
    navController: NavController,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp),
        onClick = {
            navController.navigate(NavItem.PrivacyPolicy.screenRoute)
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
fun SettingsButtonImprint(
    navController: NavController,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 12.dp),
        onClick = {
            navController.navigate(NavItem.Imprint.screenRoute)
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