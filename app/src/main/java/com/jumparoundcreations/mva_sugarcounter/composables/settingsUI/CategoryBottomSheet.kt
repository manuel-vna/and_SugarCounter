package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet() {

    val settingsVM: SettingsVM = koinViewModel()
    val categoryBottomSheet by settingsVM.categoryBottomSheetShown.collectAsState()
    val clickedCategory by settingsVM.clickedCategory.collectAsState()

    if (categoryBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                settingsVM.actionChangeCategoryBottomSheetShown(
                    false,
                    null
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(bottom = 24.dp),
                    text = clickedCategory.category,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    modifier = Modifier.padding(bottom = 120.dp),
                    text = stringResource(
                        R.string.category_listing_bottom_sheet_barcode,
                        clickedCategory.barcodeNumber
                    )
                )
            }
        }
    }

}