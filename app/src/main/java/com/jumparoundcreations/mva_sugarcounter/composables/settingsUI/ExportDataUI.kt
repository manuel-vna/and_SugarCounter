package com.jumparoundcreations.mva_sugarcounter.composables.settingsUI

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jumparoundcreations.mva_sugarcounter.R
import com.jumparoundcreations.mva_sugarcounter.data.ExportData
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.androidx.compose.koinViewModel


lateinit var uri: Uri

@Composable
fun SettingsButtonExportEntries(
    context: Context,
    settingsVM: SettingsVM,
    descriptionText: String,
    buttonIcon: Int,
) {

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            settingsVM.actionExportEntries(context = context, false, settingsVM)
            println("Permission 'Write External Storage' granted!")
        })

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        onClick = {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                settingsVM.actionExportEntries(
                    context = context,
                    osVersionHigherOrEqualsR = true,
                    settingsVM = settingsVM
                )
            } else {
                if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    settingsVM.actionExportEntries(
                        context = context,
                        osVersionHigherOrEqualsR = false,
                        settingsVM = settingsVM
                    )
                } else {
                    requestPermissionLauncher.launch(input = Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }) {
        Text(
            text = "$descriptionText   "
        )
        Icon(
            painter = painterResource(id = buttonIcon),
            contentDescription = "",
        )
    }
}

@Composable
fun ExportProgressIndicator(settingsVM: SettingsVM) {

    val exportProgressIndicator by settingsVM.exportProgressIndicator.collectAsState()
    val exportProgressIndicatorShown by settingsVM.exportProgressIndicatorShown.collectAsState()

    if (exportProgressIndicatorShown) {
        LinearProgressIndicator(
            modifier = Modifier
                .zIndex(1f),
            progress = {
                exportProgressIndicator
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBottomSheet(context: Context) {
    val settingsVM: SettingsVM = koinViewModel()
    val dataSuccessfullyExportedShown by settingsVM.dataSuccesfullyExportedShown.collectAsState()
    val exportSuccessful by settingsVM.exportSuccessfully.collectAsState()

    if (dataSuccessfullyExportedShown) {
        ModalBottomSheet(
            onDismissRequest = {
                settingsVM.actionChangeExportBottomSheetShown(isShown = false)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                if (exportSuccessful) {
                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.export_bottom_sheet_success)
                    )
                    Button(
                        modifier = Modifier.padding(bottom = 16.dp),
                        onClick = {
                            openFile(context = context, mimeType = "text/plain")
                        }
                    )
                    {
                        Text(stringResource(R.string.export_bottom_sheet_button))
                    }
                } else {
                    Text(
                        modifier = Modifier.padding(all = 32.dp),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.general_error)
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.export_data_error_message)
                    )
                }

            }
        }
    }
}

@SuppressLint("QueryPermissionsNeeded")
fun openFile(context: Context, mimeType: String) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val uriLocal = ExportData.uri
        uriLocal?.let { uri = it }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

        } else {
            navigateToOsDownloadsDir(context)
        }

    } else {
        navigateToOsDownloadsDir(context)
    }
}

fun navigateToOsDownloadsDir(context: Context) {
    val intent2 = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
    intent2.addFlags(FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent2)
}

