package com.example.mva_sugarcounter.composables


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mva_sugarcounter.R
import com.example.mva_sugarcounter.data.Category
import com.example.mva_sugarcounter.viewModels.SettingsVM


@Composable
fun Settings() {

    //get an instance of the ViewModel
    val settingsVM: SettingsVM = viewModel()

    //collecting states
    val settingsScreenShown by settingsVM.settingsScreenShown.collectAsState()
    val categoryListShown by settingsVM.categoryListShown.collectAsState()
    val categories by settingsVM.categories.collectAsState()

    if (settingsScreenShown) {
        SettingsScreen(settingsVM)
    }

    if (categoryListShown) {
        CategoryList(settingsVM, categories)
    }
}

@Composable
fun SettingsScreen(settingsVM: SettingsVM) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End
    ) {
        SettingsItemButton(
            settingsVM,
            stringResource(id = R.string.settingsCategoriesText),
            R.drawable.baseline_read_more_24
        )
        /*
        SettingsItemCheckbox(settingsVM,"test",)
        */
    }
}

@Composable
fun SettingsItemCheckbox(settingsVM: SettingsVM, descriptionText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = descriptionText)

        Switch(checked = true, onCheckedChange = null)
    }
}

@Composable
fun SettingsItemButton(settingsVM: SettingsVM, descriptionText: String, buttonIcon: Int) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            //modifier = Modifier.weight(7F),
            text = descriptionText
        )

        Button(
            //modifier = Modifier.weight(3F),
            onClick = {
                settingsVM.actionShowCategories()
            }) {
            Icon(
                painter = painterResource(id = buttonIcon),
                contentDescription = "",
            )
        }
    }
}

@Composable
fun CategoryList(settingsVM: SettingsVM, categories: List<Category>) {

    Column {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.weight(10F),
                text = stringResource(id = R.string.categoriesScreenDescription)
            )

            Button(
                modifier = Modifier.weight(5F),
                onClick = {
                    settingsVM.actionHideCategories()
                }) {
                Text(text = stringResource(id = R.string.close))
            }
        }



        LazyColumn {
            items(categories) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingsVM.actionShowDeleteAlertDialog(it.id)
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                )
                {
                    Text(text = it.category)
                }
            }
        }
    }

    val categoryDeleteAlertDialog by settingsVM.categoryDeleteAlertDialog.collectAsState()
    val categoryDeleteID by settingsVM.categoryDeleteId.collectAsState()

    if (categoryDeleteAlertDialog) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.deleteCategroyAlertTitle)) },
            onDismissRequest = { },
            dismissButton = {
                Button(

                    onClick = {
                        settingsVM.actionDismissAlertDialog()
                    }) {
                    Text("Cancel")
                }

            },
            confirmButton = {
                Button(

                    onClick = {
                        settingsVM.actionDeleteCategory(categoryDeleteID)
                    }) {
                    Text("Okay")
                }
            },
            text = {
                Text(stringResource(id = R.string.deleteCategroyAlertDescription))
            }
        )
    }
}



