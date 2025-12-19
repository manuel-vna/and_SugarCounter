package com.jumparoundcreations.sugarcounter.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.jumparoundcreations.sugarcounter.R
import com.jumparoundcreations.sugarcounter.database.AppDatabase
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingViewModel
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases.DeleteEntryUseCase
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases.EditDatabaseEntryUseCase
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases.FilterEntriesBySearchFieldUseCase
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases.GetEntryGroupPerDayUseCase
import com.jumparoundcreations.sugarcounter.features.entryListDisplayingFeature.useCases.ReuseEntryForTodayUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.EntrySavingViewModel
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.CheckDailyGramThresholdUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.CheckForDefaultSavingValuesUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.CheckUserInputUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.DisplayAllCategoriesUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.GetEntryByCategoryUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.SaveCategoryInDatabaseUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.SaveEntryInDatabaseUseCase
import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.useCases.ScanBarcodeUseCase
import com.jumparoundcreations.sugarcounter.viewModels.CategoryVM
import com.jumparoundcreations.sugarcounter.viewModels.HistoryVM
import com.jumparoundcreations.sugarcounter.viewModels.SettingsVM
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val appModule = module {
    single<AppDatabase> { AppDatabase.getInstance(androidApplication()) }
    viewModel {
        EntrySavingViewModel(
            scanBarcodeUseCase = get(),
            getEntryByCategoryUseCase = get(),
            saveEntryInDatabaseUseCase = get(),
            saveCategoryInDatabaseUseCase = get(),
            checkUserInputUseCase = get(),
            checkForDefaultSavingValuesUseCase = get(),
            displayAllCategoriesUseCase = get(),
            checkDailyGramThresholdUseCase = get()
        )
    }
    viewModel {
        EntryListDisplayingViewModel(
            getEntryGroupPerDayUseCase = get(),
            deleteEntryUseCase = get(),
            editDatabaseEntryUseCase = get(),
            reuseEntryForTodayUseCase = get(),
            filterEntriesBySearchFieldUseCase = get()
        )
    }
    viewModel { SettingsVM() }
    viewModel { CategoryVM() }
    viewModel { HistoryVM() }
    single { provideSharedPrefsMain(androidApplication()) }
    single(named("barcodeScanner")) { provideBarcodeScanner(androidApplication()) }
    single(named("termsAndConditions")) { provideHtmlContent(get(), R.raw.terms_and_conditions) }
    single(named("privacyPolicy")) { provideHtmlContent(get(), R.raw.privacy_policy) }
    single(named("imprint")) { provideHtmlContent(get(), R.raw.imprint) }
    single { ScanBarcodeUseCase() }
    single { GetEntryByCategoryUseCase(get()) }
    single { SaveEntryInDatabaseUseCase(get()) }
    single { SaveCategoryInDatabaseUseCase(get()) }
    single { CheckUserInputUseCase() }
    single { CheckForDefaultSavingValuesUseCase() }
    single { DisplayAllCategoriesUseCase(get()) }
    single { CheckDailyGramThresholdUseCase(get()) }
    single { GetEntryGroupPerDayUseCase(get()) }
    single { DeleteEntryUseCase(get()) }
    single { EditDatabaseEntryUseCase(get()) }
    single { ReuseEntryForTodayUseCase(get()) }
    single { FilterEntriesBySearchFieldUseCase() }
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
