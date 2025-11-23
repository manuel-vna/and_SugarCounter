package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases


import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.ScanResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ScanBarcodeUseCase : KoinComponent {

    private val barcodeScanner: GmsBarcodeScanner by inject(named("barcodeScanner"))
    private val database by inject<AppDatabase>()
    private val dao = database.appDao()


    suspend operator fun invoke(): ScanResult = suspendCoroutine { cont ->

        barcodeScanner.startScan()
            .addOnSuccessListener { barcode ->

                val raw = barcode.rawValue.orEmpty()

                if (raw.isEmpty()) {
                    cont.resume(ScanResult.Failed(Exception("Empty barcode")))
                    return@addOnSuccessListener
                }

                // Do DB lookup in coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    val category = dao.getCategoryByBarcodeNumber(raw)
                    if (category.isNullOrEmpty()) {
                        cont.resume(
                            ScanResult.NoCategoryForBarcode(
                                barcode.toString()
                            )
                        )
                    } else {
                        cont.resume(
                            ScanResult.FoundCategoryForBarcode(
                                category,
                                barcode.toString()
                            )
                        )
                    }
                }
            }
            .addOnFailureListener {
                cont.resume(ScanResult.Failed(it))
            }
    }


}