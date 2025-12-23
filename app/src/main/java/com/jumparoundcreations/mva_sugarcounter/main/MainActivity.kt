package com.jumparoundcreations.mva_sugarcounter.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jumparoundcreations.mva_sugarcounter.ui.theme.SugarCounterTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedPrefsMain by inject<SharedPreferences>()
    private var isDynamicColor by mutableStateOf(
        sharedPrefsMain.getBoolean(
            "dynamicColorActivated",
            false
        )
    )

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String?
    ) {
        if (key == "dynamicColorActivated") {
            isDynamicColor = sharedPrefsMain.getBoolean(
                "dynamicColorActivated",
                false
            )
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        sharedPrefsMain.registerOnSharedPreferenceChangeListener(this)

        setContent {
            SugarCounterTheme(dynamicColor = isDynamicColor) {
                // A surface container using the 'background' color from the theme

                val windowClass = calculateWindowSizeClass(activity = this)
                val showNavigationRail =
                    windowClass.widthSizeClass != WindowWidthSizeClass.Compact

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MainScreenView(applicationContext, showNavigationRail)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPrefsMain.unregisterOnSharedPreferenceChangeListener(this)
    }

}