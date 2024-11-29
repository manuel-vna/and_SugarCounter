package com.jumparoundcreations.mva_sugarcounter

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.jumparoundcreations.mva_sugarcounter.navigation.MainScreenView
import com.jumparoundcreations.mva_sugarcounter.ui.theme.Mva_SugarCounterTheme
import com.jumparoundcreations.mva_sugarcounter.viewModels.SettingsVM
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity(), KoinComponent {

    private val sharedPrefsMain by inject<SharedPreferences>(qualifier = named("sharedPrefsMain"))
    private val settingsVM = SettingsVM()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        // set the caloriesSwitch of SettingsUI to the correct state
        val caloriesCounterSwitchActivated = sharedPrefsMain.getBoolean(
            "caloriesCounterActivated",
            false
        )
        Log.d("Switch","caloriesCounterSwitchActivated: $caloriesCounterSwitchActivated")
        settingsVM.actionChangeCaloriesCounterSwitch(caloriesCounterSwitchActivated)

         */

        enableEdgeToEdge()

        setContent {
            Mva_SugarCounterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenView(applicationContext)
                }
            }
        }
    }


}
