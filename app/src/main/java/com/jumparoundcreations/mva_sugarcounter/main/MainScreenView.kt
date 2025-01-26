package com.jumparoundcreations.mva_sugarcounter.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.jumparoundcreations.mva_sugarcounter.navigation.BottomNavigation
import com.jumparoundcreations.mva_sugarcounter.navigation.NavigationGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenView(context: Context) {

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        bottomBar = { BottomNavigation(navController = navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Box(
            modifier = Modifier
                // padding at the 'top' is completely handled by WindowInsets.systemBars
                // padding at the 'bottom' is needed for app navigation bar
                .padding(start = 16.dp, end = 16.dp, bottom = 90.dp)
                // systemBars = system's top status bar and system's bottom navigation bar
                .windowInsetsPadding(WindowInsets.systemBars)
        )
        {
            NavigationGraph(
                navController = navController,
                snackbarHostState = snackbarHostState,
                context = context
            )
        }
    }
}

