package com.example.mva_sugarcounter.composables


import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mva_sugarcounter.R
import com.example.mva_sugarcounter.viewModels.CategoryListingVM
import com.example.mva_sugarcounter.viewModels.SettingsVM
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity


@Composable
fun Settings(context: Context) {

    //get an instance of the ViewModel
    val settingsVM: SettingsVM = viewModel()
    val categoryListingVM: CategoryListingVM = viewModel()

    //collecting states
    val settingsScreenShown by settingsVM.settingsScreenShown.collectAsState()
    val categoryScreenShown by categoryListingVM.categoryListShown.collectAsState()
    val faqScreenShown by settingsVM.faqScreenShown.collectAsState()

    if (settingsScreenShown) {
        SettingsScreen(context, settingsVM, categoryListingVM)
    }

    if (categoryScreenShown) {
        CategoriesScreen(context)
    }

    if (faqScreenShown) {
        FAQScreen()
    }

}

@Composable
fun SettingsScreen(context: Context, settingsVM: SettingsVM, categoryListingVM: CategoryListingVM) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End
    ) {
        SettingsButtonCategories(
            categoryListingVM,
            settingsVM,
            stringResource(id = R.string.settingsCategoriesText),
            R.drawable.baseline_read_more_24,
        )
        SettingsButtonThirdPartyLicenses(
            context,
            stringResource(id = R.string.settings_third_party_licenses_text),
            R.drawable.baseline_read_more_24,
        )
        SettingsButtonFAQs(
            context,
            settingsVM,
            stringResource(id = R.string.settings_button_faq_text),
            R.drawable.baseline_read_more_24,
        )
    }
}


@Composable
fun SettingsButtonCategories(
    categoryListingVM: CategoryListingVM,
    settingsVM: SettingsVM,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        onClick = {
            settingsVM.actionHideSettingsScreen()
            categoryListingVM.actionShowCategories()
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
fun SettingsButtonThirdPartyLicenses(
    context: Context,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
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
fun SettingsButtonFAQs(
    context: Context,
    settingsVM: SettingsVM,
    descriptionText: String,
    buttonIcon: Int,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        onClick = {
            settingsVM.actionHideSettingsScreen()
            settingsVM.actionShowFaqScreen()
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



