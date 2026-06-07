package com.jumparoundcreations.mva_sugarcounter.features.settingsFeature.useCases

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.jumparoundcreations.mva_sugarcounter.data.settingsData.ExportData
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.settingsFeature.SettingsVM
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportEntriesUseCase {
    @RequiresApi(Build.VERSION_CODES.Q)
    operator fun invoke(
        context: Context,
        osVersionHigherOrEqualsR: Boolean,
        settingsVM: SettingsVM,
        database: AppDatabase,
    ) {
        val timestamp = System.currentTimeMillis()
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault())
        val timestampString = sdf.format(date)

        val allEntriesSugar = database.appDao().getAllEntries()

        if (osVersionHigherOrEqualsR) {
            // export sugar entries for OS versions higher R
            val fileNameSugar = "sugarCounter-$timestampString"
            ExportData.exportEntriesViaMediaStore(
                context = context,
                allEntries = allEntriesSugar,
                fileName = fileNameSugar,
                settingsVM = settingsVM,
                header = "Date,Name,Mode,GramPerHundred,QuantityInGram,GramPerPiece,AmountAsANumber,Gram total\n",
            )
        } else {
            // export sugar entries
            val fileNameSugar = "sugarCounter-$timestampString"
            ExportData.exportEntriesViaFileWriter(
                allEntries = allEntriesSugar,
                fileName = fileNameSugar,
                settingsVM = settingsVM,
                header = "Date,Name,Mode,Gram perHundred/perPiece,QuantityGram/AmountNumber,GramTotal\n",
            )
        }
        println("PermissionGranted, osVersionHigherOrEqualsR: $osVersionHigherOrEqualsR")
    }
}
