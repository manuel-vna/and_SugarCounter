package com.jumparoundcreations.mva_sugarcounter.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileWriter
import java.io.IOException

object ExportData : KoinComponent {

    private val database by inject<AppDatabase>()


    fun exportEntriesViaFileWriter(context: Context, fileName: String) {

        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val csvFile = File(downloadsDir, fileName)


        try {
            val writer = FileWriter(csvFile)
            writer.append("ID,Name,Email\n") // Write the header

            val entries = database.appDao().getAllEntries()
            for (entry in entries) {
                writer.append("${entry.id},${entry.category},${entry.date}\n")
            }

            writer.flush()
            writer.close()

            println("Data successfully exported to ${csvFile.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            println("Failed to export data: ${e.message}")
        }

    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun exportEntriesViaMediaStore(context: Context, allEntries: List<Entry>, fileName: String) {

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri: Uri? = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        )
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->

                for (entry in allEntries) {
                    outputStream.write("${entry.id},${entry.category},${entry.date}\n".toByteArray())
                }

            }
        }

    }


}