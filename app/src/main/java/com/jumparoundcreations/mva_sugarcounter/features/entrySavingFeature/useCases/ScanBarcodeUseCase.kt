package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.useCases

import android.content.Context
import com.google.android.gms.common.api.OptionalModuleApi
import com.google.android.gms.common.moduleinstall.ModuleAvailabilityResponse
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.common.moduleinstall.ModuleInstallResponse
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.ScanResult
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class ScanBarcodeUseCase : KoinComponent {
    private val barcodeScanner: GmsBarcodeScanner by inject(named("barcodeScanner"))
    private val context: Context by inject()
    private val database by inject<AppDatabase>()
    private val dao = database.appDao()

    suspend operator fun invoke(): ScanResult =
        try {
            val moduleInstallClient = ModuleInstall.getClient(context)
            val optionalModuleApi = barcodeScanner as OptionalModuleApi
            val availabilityResponse = moduleInstallClient.areModulesAvailable(optionalModuleApi).await<ModuleAvailabilityResponse>()

            if (!availabilityResponse.areModulesAvailable()) {
                val moduleInstallRequest =
                    ModuleInstallRequest
                        .newBuilder()
                        .addApi(optionalModuleApi)
                        .build()

                moduleInstallClient.installModules(moduleInstallRequest).await<ModuleInstallResponse>()
            }

            val barcode = barcodeScanner.startScan().await<Barcode>()
            val raw = barcode.rawValue.orEmpty()

            if (raw.isEmpty()) {
                ScanResult.Failed(Exception("Empty barcode"))
            } else {
                val category = dao.getCategoryByBarcodeNumber(raw)
                if (category.isNullOrEmpty()) {
                    ScanResult.ScanResultNoEntryInDbForBarcode(raw)
                } else {
                    ScanResult.FoundCategoryForBarcode(category, raw)
                }
            }
        } catch (e: Exception) {
            println("Scan failed or module installation failed: ${e.message}")
            ScanResult.Failed(e)
        }
}
