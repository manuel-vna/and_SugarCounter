package com.jumparoundcreations.sugarcounter.composables

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.composables.sharedUI.SharedCardItem
import com.jumparoundcreations.sugarcounter.data.Entry
import com.jumparoundcreations.sugarcounter.data.EntryCalories
import com.jumparoundcreations.sugarcounter.data.EntryGroup
import com.jumparoundcreations.sugarcounter.util.HelperMethods
import com.jumparoundcreations.sugarcounter.viewModels.SharedVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ShowSharedCards(
    entryGroup: EntryGroup,
    backgroundColorPrimary: Boolean,
    sharedPrefsMain: SharedPreferences = koinInject()
) {

    val sharedVM: SharedVM = koinViewModel()
    val totalGramPerDayBlock = HelperMethods.calculateTotalGramPerDayBlock(
        entryGroup.entryList
    )
    var thresholdValue = 0

    if (entryGroup.entryList.isNotEmpty()) {
        thresholdValue = when (entryGroup.entryList.first()) {
            is Entry -> sharedPrefsMain.getInt("gramThresholdValue", 50)
            is EntryCalories -> sharedPrefsMain.getInt("caloriesThresholdValue", 2250)
            else -> 0
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp),
        border = if (totalGramPerDayBlock > thresholdValue) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.error)
        } else {
            BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        },
        colors = CardDefaults.cardColors(
            containerColor = if (backgroundColorPrimary)
                MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surface,
        )
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp),
        ) {

            Text(
                modifier = Modifier.padding(top = 6.dp), text = when (
                    entryGroup.dayDisplayFormat) {
                    "TODAY" -> stringResource(R.string.timestampToday)
                    "YESTERDAY" -> stringResource(
                        R.string.timestampYesterday
                    )

                    else -> entryGroup.dayDisplayFormat
                }, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontStyle = FontStyle.Italic
            )

        }

        entryGroup.entryList.forEach {

            var firstColumnText = ""
            val secondColumnText = it.category
            var thirdColumnText = ""

            when (it) {
                is Entry -> {
                    firstColumnText = if (it.isPerHundred) {
                        it.perHundredQuantity.toString() +
                                stringResource(id = R.string.gram_unit_short)
                    } else {
                        it.perPieceAmount.toString() +
                                stringResource(id = R.string.sugar_card_multiplier_expression) +
                                it.perPieceGram.toString() +
                                stringResource(id = R.string.gram_unit_short)
                    }
                    thirdColumnText =
                        it.gramTotal.toString() + stringResource(id = R.string.gram_unit_short)
                }

                is EntryCalories -> {
                    firstColumnText =
                        if (it.caloriesPerPiece == 0) {
                            stringResource(R.string.calories_textfield_placeholder)
                        } else {
                            it.caloriesAmount.toString() +
                                    stringResource(id = R.string.sugar_card_multiplier_expression) +
                                    it.caloriesPerPiece.toString()
                        }
                    thirdColumnText = it.caloriesTotal.toString()
                }

                else -> {
                    firstColumnText = ""
                    thirdColumnText = ""
                }

            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        sharedVM.actionShowCardItem(it)
                    },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(3f),
                    textAlign = TextAlign.Start,
                    text = firstColumnText,

                    )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .weight(8f),
                    textAlign = TextAlign.Center,
                    text = secondColumnText,
                )
                Text(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(2f),
                    textAlign = TextAlign.End,
                    text = thirdColumnText,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .padding(top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                painter = if (totalGramPerDayBlock <= thresholdValue)
                    painterResource(id = R.drawable.baseline_check_circle_outline_24)
                else painterResource(
                    id = R.drawable.baseline_remove_circle_outline_24
                ),
                tint = if (totalGramPerDayBlock > thresholdValue)
                    MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.secondary,
                contentDescription = stringResource(R.string.accessibility_card_status),
            )

            Text(
                modifier = Modifier.padding(end = 8.dp),
                text =
                    stringResource(id = R.string.totalAmountSugar) +
                            stringResource(id = R.string.general_colon_character) +
                            stringResource(id = R.string.general_whitespace_character) +
                            HelperMethods.calculateTotalGramPerDayBlock(
                                entryGroup.entryList
                            ),
                fontWeight = FontWeight.Bold
            )
        }

    }

    // Card Item Bottom Sheet
    val showCardItemBottomSheet by sharedVM.showCardItemBottomSheet.collectAsState()
    val cardItemToShowSugar by sharedVM.cardItemToShowSugar.collectAsState()
    val cardItemToShowCalories by sharedVM.cardItemToShowCalories.collectAsState()
    val cardItemToShowIsEntrySugar by sharedVM.cardItemToShowIsEntrySugar.collectAsState()

    if (showCardItemBottomSheet) {
        SharedCardItem(
            isEntrySugar = cardItemToShowIsEntrySugar,
            entrySugar = cardItemToShowSugar,
            entryCalories = cardItemToShowCalories
        )
    }

}











