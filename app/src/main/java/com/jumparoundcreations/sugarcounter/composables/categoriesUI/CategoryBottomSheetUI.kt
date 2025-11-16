package com.jumparoundcreations.sugarcounter.composables.categoriesUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.data.counterData.GramCountMode
import com.jumparoundcreations.sugarcounter.viewModels.CategoryVM
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet() {

    val categoryVM: CategoryVM = koinViewModel()
    val categoryBottomSheet by categoryVM.categoryBottomSheetShown.collectAsState()
    val clickedCategory by categoryVM.clickedCategory.collectAsState()
    val entrySugarForClickedCategory by categoryVM.entrySugarForClickedCategory.collectAsState()

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
                    modifier = Modifier.padding(bottom = 36.dp),
                    text = clickedCategory.category,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                if (entrySugarForClickedCategory.category.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp),
                            text = stringResource(R.string.category_bottom_sheet_sugar_value)
                        )
                        Text(
                            modifier = Modifier.padding(end = 24.dp, bottom = 16.dp),
                            text =
                                if (entrySugarForClickedCategory.entryType == GramCountMode.PerHundred) stringResource(
                                R.string.category_bottom_sheet_per_hundred_gram,
                                    entrySugarForClickedCategory.gram
                            )
                            else stringResource(
                                R.string.category_bottom_sheet_per_piece_gram,
                                    entrySugarForClickedCategory.gram
                            )
                        )
                    }
                }


                if (clickedCategory.barcodeNumber.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp),
                            text = stringResource(
                                R.string.category_bottom_sheet_barcode
                            )
                        )
                        Text(
                            modifier = Modifier.padding(end = 24.dp, bottom = 16.dp),
                            text = clickedCategory.barcodeNumber
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(60.dp))

            }
        }
    }
}
