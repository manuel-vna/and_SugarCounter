package com.jumparoundcreations.sugarcounter.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.viewModels.CategoryVM
import com.jumparoundcreations.sugarcounter.viewModels.CounterVM
import com.jumparoundcreations.sugarcounter.viewModels.HistoryVM
import com.jumparoundcreations.sugarcounter.viewModels.SettingsVM
import com.jumparoundcreations.sugarcounter.viewModels.SharedVM
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val appModule = module {
    single<AppDatabase> { AppDatabase.getInstance(androidApplication()) }
    viewModel { SettingsVM() }
    viewModel { CategoryVM() }
    viewModel { SharedVM() }
    single { CounterVM() }
    single { HistoryVM() }
    single { provideSharedPrefsMain(androidApplication()) }
    single(named("barcodeScanner")) { provideBarcodeScanner(androidApplication()) }
    single(named("termsAndConditions")) { provideHtmlContent(get(), R.raw.terms_and_conditions) }
    single(named("privacyPolicy")) { provideHtmlContent(get(), R.raw.privacy_policy) }
    single(named("imprint")) { provideHtmlContent(get(), R.raw.imprint) }
}

fun provideSharedPrefsMain(application: Application): SharedPreferences =
    application.getSharedPreferences("SHARED_PREFS_MAIN", Context.MODE_PRIVATE)

fun provideBarcodeScanner(application: Application): GmsBarcodeScanner {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            com.google.mlkit.vision.barcode.common.Barcode.FORMAT_EAN_13,
            com.google.mlkit.vision.barcode.common.Barcode.FORMAT_EAN_8,
        )
        .enableAutoZoom()
        .build()
    val scanner = GmsBarcodeScanning.getClient(application, options)
    return scanner
}

private fun provideHtmlContent(context: Context, resourceId: Int): String {
    return context.resources.openRawResource(resourceId)
        .bufferedReader().use { it.readText() }
}
