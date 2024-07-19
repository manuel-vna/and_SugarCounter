package com.example.mva_sugarcounter.di

import com.example.mva_sugarcounter.database.AppDatabase
import com.example.mva_sugarcounter.viewModels.CategoryListingVM
import com.example.mva_sugarcounter.viewModels.CounterVM
import com.example.mva_sugarcounter.viewModels.HistoryVM
import com.example.mva_sugarcounter.viewModels.SettingsVM
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single<AppDatabase> { AppDatabase.getInstance(androidApplication()) }
    viewModel { CounterVM() }
    viewModel { SettingsVM() }
    viewModel { HistoryVM() }
    viewModel { CategoryListingVM() }
}