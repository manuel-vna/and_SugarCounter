package com.jumparoundcreations.mva_sugarcounter.composables.counterUI

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM


@Composable
fun RowScope.Barcode(
    counterVM: CounterVM,
    textColor: Color
) {

    ElevatedButton(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp),
        onClick = { counterVM.scanBarcode() },
        colors = elevatedButtonColors(
            contentColor = textColor
        ),
        border = BorderStroke(width = 1.dp, color = textColor)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = Icons.Rounded.Compress,
            contentDescription = "date",
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
fun NoBarcodeYetInfo(counterVM: CounterVM, barcodeNumber: String) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = { counterVM.actionChangeNOBarcodeInfoYetDescription(true) }) {
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
                onClick = { counterVM.removeLastBarcodeInput() }) {
                Icon(
                    modifier = Modifier
                        .size(22.dp),
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = "arrow",
                )
            }
        }
    }

    val noBarcodeInfoYetDescription by counterVM.noBarcodeYetInfoDescription.collectAsState()
    if (noBarcodeInfoYetDescription) {
        ModalBottomSheet(
            onDismissRequest = {
                counterVM.actionChangeNOBarcodeInfoYetDescription(false)
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
