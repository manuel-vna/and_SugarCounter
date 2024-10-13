package com.jumparoundcreations.mva_sugarcounter.composables.counterUI

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM

@Composable

fun RowScope.CategoryDropdownField(
    context: Context,
    counterVM: CounterVM,
    caloriesCounterActivated: Boolean,
) {

    val category by counterVM.categorySelected.collectAsState()
    val categories by counterVM.categories.collectAsState()
    val categoryFieldExpanded by counterVM.categoryFieldExpanded.collectAsState()


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
            modifier =
            if (caloriesCounterActivated) {
                Modifier
                    .padding(end = 8.dp)
                    .border(
                        width = 1.8.dp,
                        shape = RoundedCornerShape(15.dp),
                        color = Color.Transparent
                    )
                    .onGloballyPositioned { coordinates ->
                        counterVM.actionChangeCategoryFieldSize(coordinates.size.toSize())
                    }
            } else {
                Modifier
                    .padding(end = 8.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.8.dp,
                        shape = RoundedCornerShape(15.dp),
                        color = Color.Transparent
                    )
                    .onGloballyPositioned { coordinates ->
                        counterVM.actionChangeCategoryFieldSize(coordinates.size.toSize())
                    }
            },
            value = category,
            onValueChange = {
                //limit input to x characters
                if (it.count() <= 40) {
                    counterVM.actionChangeSelectedCategory(it)
                    counterVM.actionChangeCategoryFieldExpanded(true)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.categoryMaxInput),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            },
            textStyle = TextStyle(
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { counterVM.actionChangeCategoryFieldExpanded(!categoryFieldExpanded) }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "arrow",
                    )
                }
            }
        )

        AnimatedVisibility(visible = categoryFieldExpanded) {
            Card(
                modifier =
                if (caloriesCounterActivated) {
                    Modifier
                        .padding(horizontal = 5.dp)
                } else {
                    Modifier
                        .padding(horizontal = 5.dp)
                        .fillMaxWidth()
                },
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                shape = RoundedCornerShape(10.dp)
            ) {

                LazyColumn(
                    modifier = Modifier.heightIn(max = 150.dp),
                ) {

                    if (category.isNotEmpty()) {
                        items(
                            categories.filter {
                                it.lowercase()
                                    .contains(category.lowercase())
                            }
                                .sorted()
                        ) {
                            CategoryItems(title = it) { title ->
                                counterVM.actionChangeSelectedCategory(title)
                                counterVM.actionChangeCategoryFieldExpanded(false)
                                counterVM.loadLastEntryForGivenCategory()
                            }
                        }
                    } else {
                        items(
                            categories.sorted()
                        ) {
                            CategoryItems(title = it) { title ->
                                counterVM.actionChangeSelectedCategory(title)
                                counterVM.actionChangeCategoryFieldExpanded(false)
                                counterVM.loadLastEntryForGivenCategory()
                            }
                        }
                    }
                } //LazyColumn
            } //Card
        } //Animated Visibility

    } // Column

}

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