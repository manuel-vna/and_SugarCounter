package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import android.content.Context
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
import androidx.compose.material.icons.rounded.Delete
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.SharedToppAppBar
import com.jumparoundcreations.mva_sugarcounter.data.Category
import com.jumparoundcreations.mva_sugarcounter.data.states.CategoryListingStates
import com.jumparoundcreations.mva_sugarcounter.viewModels.CategoryListingVM
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel


@Composable
fun CategoriesScreen(context: Context) {

    //get an instance of the ViewModel
    val categoryListingVM: CategoryListingVM = koinViewModel()
    val settingsVM: SettingsVM = koinViewModel()

    val categoryListShown by categoryListingVM.categoryListShown.collectAsState()
    val categories by categoryListingVM.categories.collectAsState()
    val deletionCheckboxes by categoryListingVM.deletionCheckboxes.collectAsState()

    if (categoryListShown) {
        CategoryList(context, categoryListingVM, settingsVM, categories, deletionCheckboxes)
    }

    CategoryBottomSheet()
}

@Composable
fun CategoryList(
    context: Context,
    categoryListingVM: CategoryListingVM,
    settingsVM: SettingsVM,
    categories: Map<Char, List<Category>>,
    deletionCheckboxes: CategoryListingStates
) {

    Column {

        SharedToppAppBar(appBarTitle = stringResource(R.string.categoriesScreenDescription),
            onBackClickAction = {
                categoryListingVM.actionHideCategories()
                settingsVM.actionShowSettingsScreen()
            },
            actionIconFirst = {
                IconButton(
                    onClick = {
                        if (deletionCheckboxes.deletionCheckboxesDisplayed) {
                            categoryListingVM.actionHideDeletionCheckboxes()
                        } else {
                            categoryListingVM.actionShowDeletionCheckboxes()
                        }
                    }) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "arrow",
                    )
                }
            }
        )

        Divider(modifier = Modifier.padding(8.dp), thickness = 3.dp)

        if (deletionCheckboxes.deletionCheckboxesDisplayed) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = { categoryListingVM.actionDeleteCheckedCategories() }
                ) {
                    Text(stringResource(id = R.string.general_delete))
                }

            }
        }

        LazyColumn {
            items(categories.toList()) { (key, value) ->

                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 4.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    text = key.toString()
                )

                Column {
                    value.forEach { category ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    settingsVM.actionChangeCategoryBottomSheetShown(
                                        true,
                                        category
                                    )
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = category.category)
                            if (deletionCheckboxes.deletionCheckboxesDisplayed) {
                                Checkbox(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 12.dp),
                                    checked = category.deletionCheckbox,
                                    onCheckedChange = {
                                        categoryListingVM.actionChangeDeleteCheckbox(category)
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}