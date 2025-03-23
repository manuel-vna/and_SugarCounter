package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.BottomSheetsSettings
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDonationUI(
    context: Context,
    settingsVM: SettingsVM
) {

    val bottomSheetsSettings by settingsVM.bottomSheetsSettings.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (bottomSheetsSettings == BottomSheetsSettings.DONATION) {

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.NONE) }
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = stringResource(R.string.settings_donation_title),
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = stringResource(R.string.settings_donation_description)
                    )

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    FilledTonalButton(
                        modifier = Modifier.weight(1F),
                        onClick = {
                            val webPageIntent = Intent(
                                Intent.ACTION_VIEW,
                                "https://www.paypal.com/donate/?hosted_button_id=BQ6GNNNZ9FAXG".toUri()
                            )
                            webPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(webPageIntent)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.settings_donation_paypal)
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(32.dp)
                            .padding(horizontal = 16.dp),
                        thickness = 4.dp
                    )

                    FilledTonalButton(
                        modifier = Modifier.weight(1F),
                        onClick = {
                            val webPageIntent = Intent(
                                Intent.ACTION_VIEW,
                                "https://buymeacoffee.com/manuelva".toUri()
                            )
                            webPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(webPageIntent)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.settings_donation_buymeacoffee)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { settingsVM.actionChangeBottomSheetsSetting(BottomSheetsSettings.NONE) }
                    ) {
                        Text(
                            text = stringResource(R.string.generalClose)
                        )
                    }
                }

            }

        }

    }

}