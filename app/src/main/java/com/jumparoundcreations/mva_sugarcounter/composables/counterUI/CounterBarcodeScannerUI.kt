package com.jumparoundcreations.mva_sugarcounter.composables.counterUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM


@Composable
fun Barcode(counterVM: CounterVM) {
    Button(
        onClick = { counterVM.scanBarcode() }) {
        Text(text = stringResource(id = R.string.scan_barcode_button))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoBarcodeYetInfo(counterVM: CounterVM, barcodeNumber: String) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
                text = stringResource(id = R.string.no_barcode_yet_info_title, barcodeNumber)
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
                Icon(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.CenterHorizontally),
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "info",
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
