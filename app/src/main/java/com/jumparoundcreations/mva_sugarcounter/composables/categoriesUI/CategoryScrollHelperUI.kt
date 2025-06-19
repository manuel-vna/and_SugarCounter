package com.jumparoundcreations.mva_sugarcounter.composables.categoriesUI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPasteSearch
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.CategoryVM


@Composable
fun ScrollToSpecificLetter(categoryVM: CategoryVM, scrollState: LazyListState) {
    val scrollSearchMenuExpanded = categoryVM.scrollSearchMenuExpanded.collectAsState()

    Box(
        modifier = Modifier
            .padding(4.dp),
    ) {
        IconButton(onClick = {
            categoryVM.actionChangeScrollSearchMenuExpanded(
                scrollSearchMenuExpanded.value.not()
            )
        }) {
            Icon(
                imageVector = Icons.Default.ContentPasteSearch,
                contentDescription = stringResource(R.string.accessibility_categories_srolling)
            )
        }
    }

    DropdownMenu(
        modifier = Modifier.width(55.dp),
        expanded = scrollSearchMenuExpanded.value,
        onDismissRequest = { categoryVM.actionChangeScrollSearchMenuExpanded(false) },
        offset = DpOffset(16.dp, 0.dp)
    ) {
        categoryVM.letters.forEach { letter ->
            DropdownMenuItem(
                modifier = Modifier.height(40.dp),
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = letter.key, fontSize = 16.sp)
                    }
                },
                onClick = {
                    categoryVM.actionHandleCategoryScrollState(
                        index = letter.value,
                        scrollState = scrollState
                    )
                    categoryVM.actionChangeScrollSearchMenuExpanded(false)
                })
        }
    }
}