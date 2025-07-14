package com.jumparoundcreations.mva_sugarcounter.main

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jumparoundcreations.mva_sugarcounter.navigation.BottomNavItem
import com.jumparoundcreations.mva_sugarcounter.navigation.BottomNavigation
import com.jumparoundcreations.mva_sugarcounter.navigation.NavigationGraph


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MainScreenView(
    context: Context,
    showNavigationRail: Boolean
) {

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val items =
        listOf(
            BottomNavItem.SugarCounter,
            BottomNavItem.SugarHistory,
            BottomNavItem.CategoryTitle,
            BottomNavItem.Settings
        )

    Scaffold(
        modifier = Modifier.displayCutoutPadding(),
        bottomBar = {
            if (showNavigationRail.not()) {
                BottomNavigation(navController = navController)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (showNavigationRail) {
                NavigationSideBar(
                    items = items,
                    navController = navController,
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
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
}

@Composable
fun NavigationSideBar(
    items: List<BottomNavItem>,
    navController: NavController,
) {
    NavigationRail {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationRailItem(
                selected = currentRoute == item.screenRoute,
                icon = { Icon(item.icon, contentDescription = stringResource(id = item.title)) },
                label = {
                    Text(
                        text = stringResource(id = item.title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                onClick = {
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

