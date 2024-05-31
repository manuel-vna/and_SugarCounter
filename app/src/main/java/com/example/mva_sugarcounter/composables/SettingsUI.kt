package com.example.mva_sugarcounter.composables


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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


@Composable
fun Settings(context: Context) {

    //get an instance of the ViewModel
    val settingsVM: SettingsVM = viewModel()
    val categoryListingVM: CategoryListingVM = viewModel()

    //collecting states
    val settingsScreenShown by settingsVM.settingsScreenShown.collectAsState()

    if (settingsScreenShown) {
        SettingsScreen(context, settingsVM, categoryListingVM)
    }
    CategoriesScreen(context)
}

@Composable
fun SettingsScreen(context: Context, settingsVM: SettingsVM, categoryListingVM: CategoryListingVM) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End
    ) {
        SettingsItemButton(
            context,
            settingsVM,
            categoryListingVM,
            stringResource(id = R.string.settingsCategoriesText),
            R.drawable.baseline_read_more_24
        )
    }
}

@Composable
fun SettingsItemButton(
    context: Context,
    settingsVM: SettingsVM,
    categoryListingVM: CategoryListingVM,
    descriptionText: String,
    buttonIcon: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        Text(
            text = descriptionText
        )

        Button(
            onClick = {
                settingsVM.actionHideSettingsScreen()
                categoryListingVM.actionShowCategories()

            }) {
            Icon(
                painter = painterResource(id = buttonIcon),
                contentDescription = "",
            )
        }

        //Testing Purposes: START
        Button(
            onClick = {
                settingsVM.actionAddTestData()

            }) {
            Text("Add test data")
        }
        //Testing Purposes: END

    }
}



