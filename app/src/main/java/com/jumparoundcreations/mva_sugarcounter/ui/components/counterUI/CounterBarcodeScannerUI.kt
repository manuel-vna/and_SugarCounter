package com.jumparoundcreations.mva_sugarcounter.ui.components.counterUI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ButtonDefaults.elevatedButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingIntents
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingStates
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.EntrySavingViewModel


@Composable
fun RowScope.Barcode(
    entrySavingViewModel: EntrySavingViewModel,
    textColor: Color
) {

    val accessibilityScanBarcodeButton = stringResource(R.string.accessibility_scan_barcode_button)

    ElevatedButton(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
            .semantics {
                contentDescription = accessibilityScanBarcodeButton
            },
        onClick = { entrySavingViewModel.onAction(EntrySavingIntents.ScanBarcode) },
        colors = elevatedButtonColors(
            contentColor = textColor
        ),
        border = BorderStroke(width = 1.dp, color = textColor)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Rounded.Compress,
            contentDescription = "",
        )
        Text(
            text = stringResource(id = R.string.scan_barcode_button),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeInfoSheet(
    onAction: (EntrySavingIntents) -> Unit,
    states: EntrySavingStates,
    barcodeNumber: String
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = {
                    onAction(EntrySavingIntents.ChangeBarcodeInfoSheetShown)
                }
            ) {
                Icon(
                    modifier = Modifier
                        .size(22.dp),
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "info",
                )
            }

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                text = stringResource(id = R.string.no_barcode_yet_info_card, barcodeNumber)
            )

            IconButton(
                onClick = {
                    onAction(EntrySavingIntents.ClearBarcodeData)
                }) {
                Icon(
                    modifier = Modifier
                        .size(22.dp),
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = "arrow",
                )
            }
        }
    }

    if (states.barcodeInfoSheetShown) {
        ModalBottomSheet(
            onDismissRequest = {
                onAction(EntrySavingIntents.ChangeBarcodeInfoSheetShown)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 32.dp,
                        end = 32.dp,
                        top = 4.dp,
                        bottom = 120.dp
                    )
            ) {

                Text(
                    text = stringResource(R.string.no_barcode_yet_info_title),
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(
                    modifier = Modifier.padding(
                        start = 4.dp,
                        end = 4.dp,
                        bottom = 16.dp
                    )
                )

                Text(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(
                        R.string.no_barcode_yet_info_description, barcodeNumber
                    )
                )
            }
        }
    }

}
