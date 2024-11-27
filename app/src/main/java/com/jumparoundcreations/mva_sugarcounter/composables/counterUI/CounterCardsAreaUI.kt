package com.jumparoundcreations.mva_sugarcounter.composables.counterUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.composables.ShowSharedCards
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM


@Composable
fun CounterCardsAreaUI(
    counterVM: CounterVM,
    caloriesCounterActivated: Boolean
) {

    val savedSugarCountGrouped by counterVM.sugarEntryDbRecent.collectAsState()
    val savedCaloriesCountGrouped by counterVM.caloriesEntryDbRecent.collectAsState()
    val segmentedButtonIndex by counterVM.segmentedButtonIndex.collectAsState()

    if (caloriesCounterActivated) {

        val buttonOptions = listOf(
            stringResource(id = R.string.general_sugar),
            stringResource(id = R.string.general_calories)
        )
        var selectedIndex by remember { mutableIntStateOf(0) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            Arrangement.SpaceEvenly
        ) {
            SingleChoiceSegmentedButtonRow {
                buttonOptions.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            counterVM.actionChangeSegmentedButtonIndex(index)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = 2)
                    )
                    {
                        Text(
                            text = option
                        )
                    }

                }
            }
        }
    }

    when (segmentedButtonIndex) {
        0 -> {
            LazyColumn {
                items(savedSugarCountGrouped) {
                    ShowSharedCards(
                        entryGroup = it,
                        backgroundColorPrimary = false
                    )
                }
            }
        }

        1 -> {
            LazyColumn {
                items(savedCaloriesCountGrouped) {
                    ShowSharedCards(
                        entryGroup = it,
                        backgroundColorPrimary = false
                    )
                }
            }
        }

        else -> Text("No index")
    }


}