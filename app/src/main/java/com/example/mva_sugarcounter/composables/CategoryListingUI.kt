package com.example.mva_sugarcounter.composables

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mva_sugarcounter.R
import com.example.mva_sugarcounter.data.Category
import com.example.mva_sugarcounter.viewModels.CategoryListingVM
import com.example.mva_sugarcounter.viewModels.SettingsVM


@Composable
fun CategoriesScreen(context: Context) {

    //get an instance of the ViewModel
    val categoryListingVM: CategoryListingVM = viewModel()
    val settingsVM: SettingsVM = viewModel()

    val categoryListShown by categoryListingVM.categoryListShown.collectAsState()
    val categories by categoryListingVM.categories.collectAsState()

    if (categoryListShown) {
        CategoryList(context, categoryListingVM, settingsVM, categories)
    }
}

@Composable
fun CategoryList(
    context: Context,
    categoryListingVM: CategoryListingVM,
    settingsVM: SettingsVM,
    categories: Map<Char, List<Category>>
) {


    BackHandler {
        categoryListingVM.actionHideCategories()
        settingsVM.actionShowSettingsScreen()
    }

    Column {

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.categoriesScreenDescription)
            )

            IconButton(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterEnd),
                onClick = { }) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "arrow",
                )
            }
        }

        Divider(modifier = Modifier.padding(8.dp), thickness = 3.dp)

        LazyColumn {
            items(categories.toList()) { (key, value) ->

                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 4.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    text = key.toString()
                )

                Column {
                    value.forEach {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    categoryListingVM.actionShowDeleteAlertDialog(it.id)
                                }
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                        )
                        {
                            Checkbox(checked = false, onCheckedChange = {})
                            Text(text = it.category)
                        }
                    }
                }
            }
        }
    }

    val categoryDeleteAlertDialog by categoryListingVM.categoryDeleteAlertDialog.collectAsState()
    val categoryDeleteID by categoryListingVM.categoryDeleteId.collectAsState()

    if (categoryDeleteAlertDialog) {
        androidx.compose.material3.AlertDialog(
            title = { Text(text = stringResource(id = R.string.deleteCategroyAlertTitle)) },
            onDismissRequest = { },
            dismissButton = {
                Button(

                    onClick = {
                        categoryListingVM.actionDismissAlertDialog()
                    }) {
                    Text(stringResource(id = R.string.generalCancel))
                }

            },
            confirmButton = {
                Button(

                    onClick = {
                        categoryListingVM.actionDeleteCategory(categoryDeleteID)
                    }) {
                    Text(stringResource(id = R.string.generalConfirm))
                }
            },
            text = {
                Text(stringResource(id = R.string.deleteCategroyAlertDescription))
            }
        )
    }
}