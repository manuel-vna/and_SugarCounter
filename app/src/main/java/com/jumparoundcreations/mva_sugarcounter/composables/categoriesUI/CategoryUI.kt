package com.jumparoundcreations.mva_sugarcounter.composables.categoriesUI

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.sharedUI.EmptyDataInfo
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.Category
import com.jumparoundcreations.mva_sugarcounter.data.categoryData.CategoryStates
import com.jumparoundcreations.mva_sugarcounter.viewModels.CategoryVM
import org.koin.androidx.compose.koinViewModel


@Composable
fun Categories(context: Context) {

    //get an instance of the ViewModel
    val categoryVM: CategoryVM = koinViewModel()

    val categories by categoryVM.categories.collectAsState()
    val deletionCheckboxes by categoryVM.deletionCheckboxes.collectAsState()
    val scrollState by categoryVM.categoryListScrollState.collectAsState()

    CategoryList(categoryVM, categories, deletionCheckboxes, scrollState)

    CategoryBottomSheet()
}

@Composable
fun CategoryList(
    categoryVM: CategoryVM,
    categories: Map<Char, List<Category>>,
    deletionCheckboxes: CategoryStates,
    scrollState: LazyListState
) {

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            ScrollToSpecificLetter(categoryVM = categoryVM, scrollState = scrollState)

            Text(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.categoryPlural)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val accessibilityNumberOfEntries =
                    stringResource(R.string.accessibility_number_of_entries)
                Text(
                    modifier = Modifier
                        .padding(2.dp)
                        .semantics {
                            contentDescription =
                                accessibilityNumberOfEntries + categories.flatMap { it.value }.size.toString()
                        },
                    text = categories.flatMap { it.value }.size.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(
                    modifier = Modifier
                        .padding(2.dp),
                    onClick = {
                        if (deletionCheckboxes.deletionCheckboxesDisplayed) {
                            categoryVM.actionHideDeletionCheckboxes()
                        } else {
                            categoryVM.actionShowDeletionCheckboxes()
                        }
                    }) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.accessibility_delete_categories),
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(8.dp), thickness = 3.dp)

        if (deletionCheckboxes.deletionCheckboxesDisplayed) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = { categoryVM.actionDeleteCheckedCategories() }
                ) {
                    Text(stringResource(id = R.string.general_delete))
                }

            }
        }

        LazyColumn(state = scrollState) {
            if (categories.isNotEmpty()) {
                items(categories.toList()) { (key, value) ->
                    Text(
                        modifier = Modifier.padding(start = 16.dp, end = 4.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        text = key.toString()
                    )

                    Column {
                        value.sortedBy { it.category }.forEach { category ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        categoryVM.actionChangeCategoryBottomSheetShown(
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
                                            categoryVM.actionChangeDeleteCheckbox(category)
                                        })
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillParentMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyDataInfo(stringResource(id = R.string.no_cateogries_yet_description))
                    }
                }
            }
        }
    }
}