package com.jumparoundcreations.mva_sugarcounter.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jumparoundcreations.mva_sugarcounter.navigation.BottomNavigation
import com.jumparoundcreations.mva_sugarcounter.navigation.NavItem
import com.jumparoundcreations.mva_sugarcounter.navigation.NavigationGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenView(context: Context) {

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        bottomBar = { BottomNavigation(navController = navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = { FloatingActionButton(navController = navController) },
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

@Composable
fun FloatingActionButton(navController: NavController) {
    SmallFloatingActionButton(
        onClick = { navController.navigate(NavItem.About.screenRoute) },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(Icons.Outlined.Info, "About")
    }
}