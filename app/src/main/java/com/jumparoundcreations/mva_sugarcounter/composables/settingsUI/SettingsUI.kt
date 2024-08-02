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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
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
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.DialogDoubleButton
import com.jumparoundcreations.mva_sugarcounter.viewModels.CategoryListingVM
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named


@Composable
fun Settings(context: Context) {

    //get an instance of the ViewModel
    val settingsVM: SettingsVM = koinViewModel()
    val categoryListingVM: CategoryListingVM = viewModel()

    //collecting states
    val settingsScreenShown by settingsVM.settingsScreenShown.collectAsState()
    val categoryScreenShown by categoryListingVM.categoryListShown.collectAsState()
    val faqScreenShown by settingsVM.faqScreenShown.collectAsState()

    settingsVM.actionResetGramThresholdSliderToSharedPref()

    if (settingsScreenShown) {
        SettingsScreen(context, settingsVM, categoryListingVM)
    }

    if (categoryScreenShown) {
        CategoriesScreen(context)
    }

    if (faqScreenShown) {
        FAQScreen(context)
    }

}

@Composable
fun SettingsScreen(
    context: Context,
    settingsVM: SettingsVM,
    categoryListingVM: CategoryListingVM,
    sharedPrefsMain: SharedPreferences = koinInject(qualifier = named("sharedPrefsMain"))
) {
    Column(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End,
    ) {
        SettingsSliderGramThreshold(
            sharedPrefsMain,
            settingsVM
        )

        Divider()

        SettingsButtonCategories(
            categoryListingVM,
            settingsVM,
            stringResource(id = R.string.settingsCategoriesText),
            R.drawable.baseline_read_more_24,
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
    }
}

@Composable
fun SettingsSliderGramThreshold(
    sharedPrefsMain: SharedPreferences,
    settingsVM: SettingsVM
) {

    val sliderPosition by settingsVM.gramThresholdSlider.collectAsState()
    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(id = R.string.gram_threshold_title))
        }

        Slider(
            value = sliderPosition,
            onValueChange = {
                settingsVM.actionUpdateGramThresholdSlider(it)
            },
            valueRange = 0f..100f
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(end = 12.dp),
                text = sliderPosition.toInt()
                    .toString() + " " + stringResource(id = R.string.general_gram)
            )
            Button(
                modifier = Modifier.padding(start = 12.dp),
                onClick = { settingsVM.actionGramThresholdDialogCheck(true) }
            ) {
                Text(text = stringResource(id = R.string.saveButton))
            }

        }
    }

    val gramThresholdDialogCheck by settingsVM.gramThresholdDialogCheck.collectAsState()
    if (gramThresholdDialogCheck) {
        DialogDoubleButton(
            dialogTitle = sliderPosition.toInt()
                .toString() + " " + stringResource(R.string.general_gram),
            dialogDescription = stringResource(id = R.string.gram_threshold_dialog_title),
            buttonOnConfirmText = stringResource(id = R.string.generalConfirm),
            buttonOnDismissText = stringResource(id = R.string.generalCancel),
            buttonOnConfirm = {
                settingsVM.actionGramThresholdDialogCheck(false)
                settingsVM.actionUpdateGramThresholdSharedPref()
            },
            buttonOnDismiss = { settingsVM.actionGramThresholdDialogCheck(false) }
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



