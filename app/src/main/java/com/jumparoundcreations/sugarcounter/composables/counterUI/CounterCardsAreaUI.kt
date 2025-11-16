package com.jumparoundcreations.sugarcounter.composables.counterUI

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jumparoundcreations.sugarcounter.composables.ShowSharedCards
import com.jumparoundcreations.sugarcounter.viewModels.CounterVM


@Composable
fun CounterCardsAreaUI(
    counterVM: CounterVM,
) {

    val savedSugarCountGrouped by counterVM.savedSugarCountGrouped.collectAsStateWithLifecycle()
    //val savedSugarCountGrouped by counterVM.sugarEntryDbRecent.collectAsState()
    val segmentedButtonIndex by counterVM.segmentedButtonIndex.collectAsState()

    Column {
        savedSugarCountGrouped.forEach {
            ShowSharedCards(
                entryGroup = it,
                backgroundColorPrimary = false
            )
        }
    }
}



