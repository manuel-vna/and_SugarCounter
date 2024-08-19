package com.jumparoundcreations.mva_sugarcounter.composables.categoriesUI

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
import com.jumparoundcreations.mva_sugarcounter.viewModels.CategoryVM
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet() {

    val categoryVM: CategoryVM = koinViewModel()
    val categoryBottomSheet by categoryVM.categoryBottomSheetShown.collectAsState()
    val clickedCategory by categoryVM.clickedCategory.collectAsState()
    val entryForClickedCategory by categoryVM.entryForClickedCategory.collectAsState()

    if (categoryBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                categoryVM.actionChangeCategoryBottomSheetShown(
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
                if (clickedCategory.barcodeNumber.isNotEmpty()) {
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = stringResource(
                            R.string.category_bottom_sheet_barcode,
                            clickedCategory.barcodeNumber
                        )
                    )
                }
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = "Letzter Eintrag:"
                )
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = stringResource(
                        R.string.category_bottom_sheet_per_piece_gram,
                        entryForClickedCategory.perPieceGram
                    )
                )
                Text(
                    modifier = Modifier.padding(bottom = 120.dp),
                    text = entryForClickedCategory.date
                )
            }
        }
    }

}