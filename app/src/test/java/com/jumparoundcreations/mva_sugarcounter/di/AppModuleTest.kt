package com.jumparoundcreations.mva_sugarcounter.di

import com.jumparoundcreations.mva_sugarcounter.api.networkModule
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class AppModuleTest : KoinTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkAllModules() {
        appModule.verify(
            extraTypes = listOf(
                com.jumparoundcreations.mva_sugarcounter.api.FoodApiService::class,
                android.app.Application::class,
                android.content.Context::class,
                android.content.SharedPreferences::class,
                com.google.mlkit.vision.codescanner.GmsBarcodeScanner::class,
            )
        )

        networkModule.verify(
            extraTypes = listOf(
                io.ktor.client.engine.HttpClientEngine::class,
            )
        )
    }
}
