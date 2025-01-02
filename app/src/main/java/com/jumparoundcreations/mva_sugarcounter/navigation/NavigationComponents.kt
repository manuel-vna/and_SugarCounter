package com.jumparoundcreations.mva_sugarcounter.navigation


import android.content.Context
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jumparoundcreations.mva_sugarcounter.composables.categoriesUI.Categories
import com.jumparoundcreations.mva_sugarcounter.composables.counterUI.Counter
import com.jumparoundcreations.mva_sugarcounter.composables.historyUI.History
import com.jumparoundcreations.mva_sugarcounter.composables.settingsUI.FAQScreen
import com.jumparoundcreations.mva_sugarcounter.composables.settingsUI.ImprintUI
import com.jumparoundcreations.mva_sugarcounter.composables.settingsUI.PrivacyPolicyUI
import com.jumparoundcreations.mva_sugarcounter.composables.settingsUI.Settings
import com.jumparoundcreations.mva_sugarcounter.composables.settingsUI.SettingsAboutUI
import com.jumparoundcreations.mva_sugarcounter.composables.settingsUI.TermsAndConditionsUI


@Composable
fun NavigationGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.SugarCounter.screenRoute
    ) {
        composable(route = BottomNavItem.SugarCounter.screenRoute) {
            Counter(context, snackbarHostState)
        }
        composable(route = BottomNavItem.SugarHistory.screenRoute) {
            History(context)
        }
        composable(route = BottomNavItem.CategoryTitle.screenRoute) {
            Categories(context)
        }
        composable(route = BottomNavItem.Settings.screenRoute) {
            Settings(context, navController)
        }
        composable(route = NavItem.FAQ.screenRoute) {
            FAQScreen()
        }
        composable(route = NavItem.About.screenRoute) {
            SettingsAboutUI(navController)
        }
        composable(route = NavItem.TermsAndConditions.screenRoute) {
            TermsAndConditionsUI()
        }
        composable(route = NavItem.PrivacyPolicy.screenRoute) {
            PrivacyPolicyUI()
        }
        composable(route = NavItem.Imprint.screenRoute) {
            ImprintUI()
        }

    }
}

@Composable
fun BottomNavigation(navController: NavController) {

    val items =
        listOf(
            BottomNavItem.SugarCounter,
            BottomNavItem.SugarHistory,
            BottomNavItem.CategoryTitle,
            BottomNavItem.Settings
        )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screenRoute,
                icon = { Icon(item.icon, contentDescription = stringResource(id = item.title)) },
                label = { Text(stringResource(id = item.title)) },
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