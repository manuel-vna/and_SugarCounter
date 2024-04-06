package com.example.mva_sugarcounter.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mva_sugarcounter.viewModels.CounterVM


@Composable
fun History() {

    val counterVM: CounterVM = viewModel()
    val savedSugarCountGrouped by counterVM.savedHistory.collectAsState()

    LazyColumn {
        items(
            savedSugarCountGrouped.toList().sortedByDescending { it.first.first }) { (key, value) ->
            ShowSugarCountItemsShared(key = key.second, valueList = value, false)
        }
    }
}



