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
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature.EntryListDisplayingViewModel
import com.jumparoundcreations.mva_sugarcounter.features.settingsFeature.SettingsRoute
import com.jumparoundcreations.mva_sugarcounter.ui.components.categoriesUI.Categories
import com.jumparoundcreations.mva_sugarcounter.ui.components.counterUI.Counter
import com.jumparoundcreations.mva_sugarcounter.ui.components.historyUI.History
import com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI.FAQScreen
import com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI.ImprintUI
import com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI.OnboardingUI
import com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI.PrivacyPolicyUI
import com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI.SettingsThirdPartyLibrariesUI
import com.jumparoundcreations.mva_sugarcounter.ui.components.settingsUI.TermsAndConditionsUI
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    context: Context,
    entryListDisplayingViewModel: EntryListDisplayingViewModel = koinViewModel(),
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.SugarCounter.screenRoute,
    ) {
        composable(route = BottomNavItem.SugarCounter.screenRoute) {
            Counter(
                context = context,
                snackbarHostState = snackbarHostState,
                entryListDisplayingViewModel = entryListDisplayingViewModel,
            )
        }
        composable(route = BottomNavItem.SugarHistory.screenRoute) {
            History(
                context = context,
                entryListDisplayingViewModel = entryListDisplayingViewModel,
            )
        }
        composable(route = BottomNavItem.CategoryTitle.screenRoute) {
            Categories()
        }
        composable(route = BottomNavItem.Settings.screenRoute) {
            SettingsRoute(
                context = context,
                navController = navController,
                snackbarHostState = snackbarHostState,
            )
        }
        composable(route = NavItem.FAQ.screenRoute) {
            FAQScreen(
                navController,
            )
        }
        composable(route = NavItem.ThirdPartyLibraries.screenRoute) {
            SettingsThirdPartyLibrariesUI(
                navController,
            )
        }
        composable(route = NavItem.TermsAndConditions.screenRoute) {
            TermsAndConditionsUI(
                navController,
            )
        }
        composable(route = NavItem.PrivacyPolicy.screenRoute) {
            PrivacyPolicyUI(
                navController,
            )
        }
        composable(route = NavItem.Imprint.screenRoute) {
            ImprintUI(
                navController,
            )
        }
        composable(route = NavItem.Onboarding.screenRoute) {
            OnboardingUI(
                navController,
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
            BottomNavItem.Settings,
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
                        overflow = TextOverflow.Ellipsis,
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
                },
            )
        }
    }
}
