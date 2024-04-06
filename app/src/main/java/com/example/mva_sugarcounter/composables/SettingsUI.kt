package com.example.mva_sugarcounter.composables


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
    Button(onClick = {
        settingsVM.actionShowCategories()
    }) {
        Text(text = stringResource(id = R.string.showCategories))
    }

}

@Composable
fun CategoryList(settingsVM: SettingsVM, categories: List<Category>) {

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

    Button(onClick = {
        settingsVM.actionHideCategories()
    }) {
        Text(text = stringResource(id = R.string.close))
    }
}

