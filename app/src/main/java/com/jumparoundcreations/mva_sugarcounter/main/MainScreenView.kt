package com.jumparoundcreations.mva_sugarcounter.main

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.padding
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


@Composable
fun MainScreenView(context: Context) {

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.displayCutoutPadding(),
        bottomBar = { BottomNavigation(navController = navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
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

