package com.jumparoundcreations.mva_sugarcounter.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileWriter
import java.io.IOException

object ExportData : KoinComponent {

    val database by inject<AppDatabase>()
    lateinit var csvFile: File
    var uri: Uri? = null
    private const val HeaderString =
        "Date,Name,Mode,Gram perHundred/perPiece,QuantityGram/AmountNumber,GramTotal\n"


    fun exportEntriesViaFileWriter(
        allEntries: List<Entry>,
        fileName: String,
        settingsVM: SettingsVM
    ) {

        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        csvFile = File(downloadsDir, fileName)

        try {
            val writer = FileWriter(csvFile)
            //Header
            writer.append(HeaderString)

            settingsVM.actionChangExportProgressIndicatorVisibility(isShown = true)

            //START: Variables for ProgressIndicator
            var count = 0
            var multiplier = 1
            val tenPercent = allEntries.count() / 10
            var currentStep = tenPercent
            //END: Variables for ProgressIndicator

            for (entry in allEntries) {
                if (entry.isPerHundred) {
                    writer.append("${entry.date},${entry.category},perHundred,${entry.perHundredGram},${entry.perHundredQuantity},${entry.gramTotal}\n")
                } else {
                    writer.append("${entry.date},${entry.category},perPiece,${entry.perPieceGram},${entry.perPieceAmount},${entry.gramTotal}\n")
                }

                //progress indicator
                count++
                if (currentStep == count) {
                    settingsVM.actionIncrementExportProgressIndicator()
                    multiplier++
                    currentStep = tenPercent * multiplier
                }

            }

            writer.flush()
            writer.close()

            settingsVM.actionChangExportProgressIndicatorVisibility(isShown = false)
            settingsVM.actionChangeExportBottomSheetVisibility(isShown = true)

        } catch (e: IOException) {
            e.printStackTrace()
            settingsVM.actionChangeExportSuccessfully(wasSuccessful = false)
            println("Failed to export data: ${e.message}")
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun exportEntriesViaMediaStore(
        context: Context,
        allEntries: List<Entry>,
        fileName: String,
        settingsVM: SettingsVM
    ) {

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/comma-separated-values")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            settingsVM.actionChangExportProgressIndicatorVisibility(isShown = true)

            uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->

                    // Header
                    outputStream.write(HeaderString.toByteArray())

                    //START: Variables for ProgressIndicator
                    var count = 0
                    var multiplier = 1
                    val tenPercent = allEntries.count() / 10
                    var currentStep = tenPercent
                    //END: Variables for ProgressIndicator

                    for (entry in allEntries) {
                        if (entry.isPerHundred) {
                            outputStream.write("${entry.date},${entry.category},perHundred,${entry.perHundredGram},${entry.perHundredQuantity},${entry.gramTotal}\n".toByteArray())
                        } else {
                            outputStream.write("${entry.date},${entry.category},perPiece,${entry.perPieceGram},${entry.perPieceAmount},${entry.gramTotal}\n".toByteArray())
                        }

                        //progress indicator
                        count++
                        if (currentStep == count) {
                            settingsVM.actionIncrementExportProgressIndicator()
                            multiplier++
                            currentStep = tenPercent * multiplier
                        }
                    }
                }
            }

            settingsVM.actionChangExportProgressIndicatorVisibility(isShown = false)
            settingsVM.actionChangeExportBottomSheetVisibility(isShown = true)

        } catch (e: IOException) {
            e.printStackTrace()
            settingsVM.actionChangeExportSuccessfully(wasSuccessful = false)
            println("Failed to export data: ${e.message}")
        }
    }

}