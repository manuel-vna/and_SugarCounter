package com.jumparoundcreations.mva_sugarcounter.ui.components.counterUI

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.util.NumberConstants

@Composable

fun RowScope.CategoryDropdownField(
    context: Context,
    onAction: (EntrySavingIntents) -> Unit,
    entrySavingStates: EntrySavingStates,
    keyboardController: SoftwareKeyboardController?
) {

    val accessibilityCategoryInputField =
        stringResource(R.string.accessibility_category_input_field)

    Column(
        modifier = Modifier.weight(2f)
    ) {

        Text(
            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
            text = stringResource(R.string.foodType),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        TextField(
            modifier = Modifier
                .padding(end = 8.dp)
                .fillMaxWidth()
                .border(
                    width = 1.8.dp,
                    shape = RoundedCornerShape(15.dp),
                    color = Color.Transparent
                )
                .semantics {
                    contentDescription = accessibilityCategoryInputField
                },
            value = entrySavingStates.categoryInField,
            onValueChange = {
                if (it.count() <= NumberConstants.CATEGORY_MAX_INPUT_CHARACTERS) {
                    onAction(
                        EntrySavingIntents.EditOfCategoryField(
                            categoryInField = it,
                            categoryDropdownExpanded = true
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.categoryMaxInput),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            textStyle = TextStyle(
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    onAction(
                        EntrySavingIntents.ExpandOrCollapseCategoryDropdown(
                            categoryDropdownExpanded =
                                entrySavingStates.categoryDropdownExpanded.not()
                        )
                    )
                }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.accessibility_category_arrow_list),
                    )
                }
            }
        )

        AnimatedVisibility(visible = entrySavingStates.categoryDropdownExpanded) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                shape = RoundedCornerShape(10.dp),
            ) {

                LazyColumn(
                    modifier = Modifier.heightIn(max = 150.dp),
                ) {

                    if (entrySavingStates.categoryInField.isNotEmpty()) {
                        items(
                            entrySavingStates.categoryListInDropdown.filter {
                                it.lowercase()
                                    .contains(entrySavingStates.categoryInField.lowercase())
                            }
                                .sorted()
                        ) {
                            CategoryItems(title = it) { title ->
                                onAction(
                                    EntrySavingIntents.EditOfCategoryWithinDropdown(
                                        categoryInDropdown = title,
                                        categoryDropdownExpanded = false
                                    )
                                )
                                keyboardController?.hide()
                            }
                        }
                    } else {
                        items(
                            entrySavingStates.categoryListInDropdown.sorted()
                        ) {
                            CategoryItems(title = it) { title ->
                                onAction(
                                    EntrySavingIntents.EditOfCategoryWithinDropdown(
                                        categoryInDropdown = title,
                                        categoryDropdownExpanded = false
                                    )
                                )
                                keyboardController?.hide()
                            }
                        }
                    }
                } //LazyColumn
            } //Card
        } //Animated Visibility

    } // Column

} // Composable: CategoryDropdownField

@Composable
fun CategoryItems(
    title: String,
    onSelect: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(title)
            }
            .padding(10.dp)
    ) {
        Text(text = title, fontSize = 16.sp)
    }
}