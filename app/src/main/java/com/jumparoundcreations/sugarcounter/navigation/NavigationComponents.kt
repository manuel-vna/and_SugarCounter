package com.jumparoundcreations.sugarcounter.navigation


import android.content.Context
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState


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
            _root_ide_package_.com.jumparoundcreations.sugarcounter.ui.components.counterUI.Counter(
                context,
                snackbarHostState
            )
        }
        composable(route = BottomNavItem.SugarHistory.screenRoute) {
            _root_ide_package_.com.jumparoundcreations.sugarcounter.ui.components.historyUI.History(
                context
            )
        }
        composable(route = BottomNavItem.CategoryTitle.screenRoute) {
            _root_ide_package_.com.jumparoundcreations.sugarcounter.ui.components.categoriesUI.Categories()
        }
        composable(route = BottomNavItem.Settings.screenRoute) {
            _root_ide_package_.com.jumparoundcreations.sugarcounter.ui.components.settingsUI.Settings(
                context,
                navController
            )
        }
        composable(route = NavItem.FAQ.screenRoute) {
            _root_ide_package_.com.jumparoundcreations.sugarcounter.ui.components.settingsUI.FAQScreen(
                navController
            )
        }
        composable(route = NavItem.ThirdPartyLibraries.screenRoute) {
            _root_ide_package_.com.jumparoundcreations.sugarcounter.ui.components.settingsUI.SettingsThirdPartyLibrariesUI(
                navController
            )
        }
        composable(route = NavItem.TermsAndConditions.screenRoute) {
            _root_ide_package_.com.jumparoundcreations.sugarcounter.ui.components.settingsUI.TermsAndConditionsUI(
                navController
            )
        }
        composable(route = NavItem.PrivacyPolicy.screenRoute) {
            _root_ide_package_.com.jumparoundcreations.sugarcounter.ui.components.settingsUI.PrivacyPolicyUI(
                navController
            )
        }
        composable(route = NavItem.Imprint.screenRoute) {
            _root_ide_package_.com.jumparoundcreations.sugarcounter.ui.components.settingsUI.ImprintUI(
                navController
            )
        }
        composable(route = NavItem.Onboarding.screenRoute) {
            _root_ide_package_.com.jumparoundcreations.sugarcounter.ui.components.settingsUI.OnboardingUI(
                navController
            )
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