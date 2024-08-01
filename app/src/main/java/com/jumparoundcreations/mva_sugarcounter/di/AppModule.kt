package com.jumparoundcreations.mva_sugarcounter.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.jumparoundcreations.mva_sugarcounter.database.AppDatabase
import com.jumparoundcreations.mva_sugarcounter.viewModels.CategoryListingVM
import com.jumparoundcreations.mva_sugarcounter.viewModels.CounterVM
import com.jumparoundcreations.mva_sugarcounter.viewModels.HistoryVM
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val appModule = module {
    single<AppDatabase> { AppDatabase.getInstance(androidApplication()) }
    viewModel { CounterVM() }
    viewModel { SettingsVM() }
    viewModel { HistoryVM() }
    viewModel { CategoryListingVM() }
    single(named("sharedPrefsMain")) { provideSharedPrefsMain(androidApplication()) }
}


fun provideSharedPrefsMain(application: Application): SharedPreferences =
    application.getSharedPreferences("SHARED_PREFS_MAIN", Context.MODE_PRIVATE)
