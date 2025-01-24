package com.jumparoundcreations.mva_sugarcounter.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
                .fillMaxSize()
                .padding(it)
        ) {
            NavigationGraph(
                navController = navController,
                snackbarHostState = snackbarHostState,
                context = context
            )
        }
    }
}

