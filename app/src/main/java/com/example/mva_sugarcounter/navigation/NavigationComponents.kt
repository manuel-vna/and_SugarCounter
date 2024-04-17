package com.example.mva_sugarcounter.navigation


import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mva_sugarcounter.composables.Counter
import com.example.mva_sugarcounter.composables.History
import com.example.mva_sugarcounter.composables.Settings

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenView(context: Context) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigation(navController = navController) }
    ) {
        Box(modifier = Modifier.padding(bottom = it.calculateBottomPadding())) {
            NavigationGraph(navController = navController, context)
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, context: Context) {
    NavHost(navController, startDestination = BottomNavItem.SugarCounter.screenRoute) {
        composable(BottomNavItem.SugarCounter.screenRoute) {
            Counter(context)
        }
        composable(BottomNavItem.SugarHistory.screenRoute) {
            History()
        }
        composable(BottomNavItem.Settings.screenRoute) {
            Settings(context)
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {

    val items =
        listOf(BottomNavItem.SugarCounter, BottomNavItem.SugarHistory, BottomNavItem.Settings)

    NavigationBar {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screenRoute,
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
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