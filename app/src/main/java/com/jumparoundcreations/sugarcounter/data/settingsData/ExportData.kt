package com.jumparoundcreations.sugarcounter.data.settingsData

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.jumparoundcreations.sugarcounter.data.Entry
import com.jumparoundcreations.sugarcounter.data.EntryCalories
import com.jumparoundcreations.sugarcounter.data.IEntry
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.viewModels.SettingsVM
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileWriter
import java.io.IOException

object ExportData : KoinComponent {

    val database by inject<AppDatabase>()
    lateinit var csvFile: File
    var uri: Uri? = null

    fun <T : IEntry> exportEntriesViaFileWriter(
        allEntries: List<T>,
        fileName: String,
        settingsVM: SettingsVM,
        header: String
    ) {

        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        csvFile = File(downloadsDir, fileName)

        try {
            val writer = FileWriter(csvFile)
            //Header
            writer.append(header)

            settingsVM.actionChangExportProgressIndicatorVisibility(isShown = true)

            //START: Variables for ProgressIndicator
            var count = 0
            var multiplier = 1
            val tenPercent = allEntries.count() / 10
            var currentStep = tenPercent
            //END: Variables for ProgressIndicator

            for (entry in allEntries) {

                when (entry) {
                    is Entry -> {
                        if (entry.isPerHundred) {
                            writer.append("${entry.date},${entry.category},perHundred,${entry.perHundredGram},${entry.perHundredQuantity},${entry.gramTotal}\n")
                        } else {
                            writer.append("${entry.date},${entry.category},perPiece,${entry.perPieceGram},${entry.perPieceAmount},${entry.gramTotal}\n")
                        }
                    }

                    is EntryCalories -> {
                        writer.append("${entry.date},${entry.category},${entry.caloriesTotal}\n")
                    }

                    else -> writer.append("")
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
    fun <T : IEntry> exportEntriesViaMediaStore(
        context: Context,
        allEntries: List<T>,
        fileName: String,
        settingsVM: SettingsVM,
        header: String
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
                    outputStream.write(header.toByteArray())

                    //START: Variables for ProgressIndicator
                    var count = 0
                    var multiplier = 1
                    val tenPercent = allEntries.count() / 10
                    var currentStep = tenPercent
                    //END: Variables for ProgressIndicator

                    for (entry in allEntries) {

                        when (entry) {
                            is Entry -> {
                                if (entry.isPerHundred) {
                                    outputStream.write("${entry.date},${entry.category},perHundred,${entry.perHundredGram},${entry.perHundredQuantity},${entry.gramTotal}\n".toByteArray())
                                } else {
                                    outputStream.write("${entry.date},${entry.category},perPiece,${entry.perPieceGram},${entry.perPieceAmount},${entry.gramTotal}\n".toByteArray())
                                }
                            }

                            is EntryCalories -> {
                                outputStream.write("${entry.date},${entry.category},${entry.caloriesTotal}\n".toByteArray())
                            }

                            else -> outputStream.write("".toByteArray())
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