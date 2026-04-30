package com.jumparoundcreations.mva_sugarcounter.ui.components.entryListUI

import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.Screens
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.SuccessData
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode
import com.jumparoundcreations.mva_sugarcounter.util.HelperMethods
import org.koin.compose.koinInject
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun EntryListUI(
    currentScreen: Screens,
    backgroundColorPrimary: Boolean,
    data: SuccessData,
    onAction: (EntryListDisplayingIntents) -> Unit,
    sharedPrefsMain: SharedPreferences = koinInject()
) {

    val entryGroupList = if (currentScreen == Screens.COUNTER) {
        data.entriesGroupedPerDayCounter
    } else {
        data.entriesGroupedPerDayHistory
    }

    if (currentScreen == Screens.HISTORY && data.entriesGroupedPerDayHistory.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmptyDataInfo(stringResource(id = R.string.no_cards_yet_description))
        }
    } else {
        Column {

            var previousMonth: YearMonth? = null

            entryGroupList.forEach { entryGroup ->

                if (currentScreen != Screens.COUNTER) {
                    val currentMonth: YearMonth =
                        HelperMethods.yearMonthFromIsoDate(dateStr = entryGroup.date)
                    if (previousMonth == null || currentMonth != previousMonth) {
                        Text(
                            text = currentMonth.format(
                                DateTimeFormatter.ofPattern("MMMM uuuu")
                            ),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 12.dp, bottom = 4.dp)
                        )
                    }
                    previousMonth = currentMonth
                }

                val totalGramPerDayBlock = HelperMethods.calculateTotalGramPerDayBlock(
                    entryGroup.entryList
                )
                var thresholdValue = 0

                if (entryGroup.entryList.isNotEmpty()) {
                    thresholdValue = sharedPrefsMain.getInt("gramThresholdValue", 50)
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
                            modifier = Modifier.padding(top = 6.dp),
                            text = when (
                                entryGroup.dayDisplayFormat) {
                                "TODAY" -> stringResource(R.string.timestampToday)
                                "YESTERDAY" -> stringResource(
                                    R.string.timestampYesterday
                                )

                                else -> entryGroup.dayDisplayFormat
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            fontStyle = FontStyle.Italic
                        )

                    }

                    entryGroup.entryList.forEach {

                        var firstColumnText = ""
                        val secondColumnText = it.category
                        var thirdColumnText = ""

                        firstColumnText = if (it.entryType == GramCountMode.PerHundred) {
                            it.quantity.toString() + stringResource(id = R.string.gram_unit_short)
                        } else {
                            it.quantity.toString() +
                                    stringResource(id = R.string.sugar_card_multiplier_expression) +
                                    it.gram.toString() +
                                    stringResource(id = R.string.gram_unit_short)
                        }
                        thirdColumnText =
                            it.gramTotal.toString() + stringResource(id = R.string.gram_unit_short)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAction(
                                        EntryListDisplayingIntents.OpenCardDetails(
                                            sugarEntry = it
                                        )
                                    )
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
                                    .weight(6f),
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
                                        "%.1f".format(
                                            HelperMethods.calculateTotalGramPerDayBlock(
                                                entryGroup.entryList
                                            )
                                        ),
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
        }
    }

// Card Item Bottom Sheet
    if (data.showCardItemBottomSheet) {
        EntryListItemUI(
            data = data,
            onAction = onAction,
        )
    }
}











