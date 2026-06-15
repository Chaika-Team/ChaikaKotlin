package com.chaikasoft.app.ui.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chaikasoft.app.ui.components.bottombar.BottomBar
import com.chaikasoft.app.ui.components.bottombar.BottomBarTab
import com.chaikasoft.app.ui.components.bottombar.HistoricalTripBottomBar
import com.chaikasoft.app.ui.components.bottombar.HistoricalTripBottomBarTab
import com.chaikasoft.app.ui.components.bottombar.NavigationBlockedBottomSheet
import com.chaikasoft.app.ui.components.topbar.MenuItem
import com.chaikasoft.app.ui.components.topbar.TopBar
import com.chaikasoft.app.ui.navigation.NavGraph
import com.chaikasoft.app.ui.navigation.NavigationGuards
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.navigation.Screen
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.viewmodels.AuthViewModel
import com.chaikasoft.app.ui.viewmodels.MainNavigationViewModel
import com.chaikasoft.app.ui.viewmodels.TripViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChaikaTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val tripViewModel: TripViewModel = hiltViewModel()
                val mainNavigationViewModel: MainNavigationViewModel = hiltViewModel()
                val hasActiveShift by
                    mainNavigationViewModel.hasActiveShift.collectAsStateWithLifecycle()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val historicalShiftUuid =
                    navBackStackEntry?.arguments?.getString(Routes.ARG_SHIFT_UUID)
                val currentScreen = Screen.fromRoute(currentRoute)
                var showBlockedNavigationSheet by remember { mutableStateOf(false) }
                var openFinishTripConfirmOnTripMain by remember { mutableStateOf(false) }

                // Управление ориентацией экрана в зависимости от текущего маршрута
                LaunchedEffect(currentRoute) {
                    requestedOrientation = when {
                        currentRoute == Routes.STATISTICS ||
                            Routes.isHistoricalStatisticsRoute(currentRoute) ->
                            ActivityInfo.SCREEN_ORIENTATION_SENSOR
                        else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                    if (currentRoute == Routes.TRIP_MAIN) {
                        mainNavigationViewModel.refreshAuthenticatedAppSilently()
                    }
                }

                val menuItems = when (currentScreen) {
                    is Screen.Package -> listOf(MenuItem.REFILL)
                    is Screen.FindTripByNumber -> listOf(MenuItem.AUTONOMOUS_TRIP)
                    else -> emptyList()
                }

                Scaffold(
                    topBar = {
                        if (currentRoute !in Routes.routesWithoutTopBar) {
                            TopBar(
                                currentScreen = currentScreen,
                                menuItems = if (currentScreen.showMenuIcon) {
                                    menuItems
                                } else {
                                    emptyList()
                                },
                                onBackClick = {
                                    if (Routes.isHistoricalRoute(currentRoute)) {
                                        navController.popBackStack(
                                            Routes.TRIP_MAIN,
                                            inclusive = false
                                        )
                                    } else {
                                        navController.navigateUp()
                                    }
                                },
                                onMenuItemClick = { item ->
                                    when (item) {
                                        MenuItem.REFILL -> navController.navigate(
                                            Routes.PRODUCT_REPLENISH
                                        )

                                        MenuItem.AUTONOMOUS_TRIP -> navController.navigate(
                                            Routes.TRIP_AUTONOMOUS
                                        )
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (Routes.isHistoricalRoute(currentRoute) && historicalShiftUuid != null) {
                            HistoricalTripBottomBar(
                                selectedTab = currentHistoricalBottomBarTab(currentRoute),
                                onTabClick = { tab ->
                                    navController.navigateToHistoricalTab(
                                        tab = tab,
                                        shiftUuid = historicalShiftUuid,
                                        currentRoute = currentRoute
                                    )
                                }
                            )
                        } else if (currentRoute != null &&
                            currentRoute !in Routes.routesWithoutBottomBar
                        ) {
                            BottomBar(
                                selectedTab = currentBottomBarTab(currentRoute),
                                onTabClick = { tab ->
                                    navController.navigateToMainTab(
                                        tab = tab,
                                        hasActiveShift = hasActiveShift,
                                        onBlockedNavigation = {
                                            showBlockedNavigationSheet = true
                                        }
                                    )
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .semantics { testTagsAsResourceId = true }
                    ) {
                        NavGraph(
                            navController = navController,
                            authViewModel = authViewModel,
                            tripViewModel = tripViewModel,
                            hasActiveShift = hasActiveShift,
                            currentRoute = currentRoute,
                            openFinishTripConfirmOnTripMain = openFinishTripConfirmOnTripMain,
                            onFinishTripConfirmConsumed = {
                                openFinishTripConfirmOnTripMain = false
                            },
                            onNavigateToTripCompletion = {
                                openFinishTripConfirmOnTripMain = true
                                navController.navigate(Routes.TRIP_MAIN) {
                                    popUpTo(Routes.TRIP_GRAPH) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }

                NavigationBlockedBottomSheet(
                    visible = showBlockedNavigationSheet,
                    onDismiss = { showBlockedNavigationSheet = false }
                )
            }
        }
    }
}

private fun currentBottomBarTab(currentRoute: String?): BottomBarTab? = when {
    Routes.mainRoutes[Routes.TRIP]?.contains(currentRoute) == true -> BottomBarTab.TRIP
    Routes.mainRoutes[Routes.PRODUCT]?.contains(currentRoute) == true -> BottomBarTab.PRODUCT
    Routes.mainRoutes[Routes.STATISTICS]?.contains(currentRoute) == true -> BottomBarTab.STATISTICS
    Routes.mainRoutes[Routes.OPERATION]?.contains(currentRoute) == true -> BottomBarTab.OPERATION
    Routes.mainRoutes[Routes.PROFILE]?.contains(currentRoute) == true -> BottomBarTab.PROFILE
    else -> null
}

private fun currentHistoricalBottomBarTab(currentRoute: String?): HistoricalTripBottomBarTab? =
    when {
        Routes.isHistoricalStatisticsRoute(currentRoute) -> HistoricalTripBottomBarTab.STATISTICS
        Routes.isHistoricalOperationsRoute(currentRoute) -> HistoricalTripBottomBarTab.OPERATIONS
        else -> null
    }

private fun NavHostController.navigateToMainTab(
    tab: BottomBarTab,
    hasActiveShift: Boolean,
    onBlockedNavigation: () -> Unit
) {
    val route = when (tab) {
        BottomBarTab.TRIP -> Routes.TRIP_MAIN
        BottomBarTab.PRODUCT -> Routes.PRODUCT_GRAPH
        BottomBarTab.STATISTICS -> Routes.STATISTICS_GRAPH
        BottomBarTab.OPERATION -> Routes.OPERATION_GRAPH
        BottomBarTab.PROFILE -> Routes.PROFILE
    }
    val popUpToRoute = when (tab) {
        BottomBarTab.TRIP -> Routes.TRIP_GRAPH
        BottomBarTab.PROFILE -> Routes.PROFILE_GRAPH
        else -> route
    }

    if (NavigationGuards.isProtectedBottomGraph(route) && !hasActiveShift) {
        onBlockedNavigation()
        return
    }

    navigate(route) {
        popUpTo(popUpToRoute) { inclusive = route == popUpToRoute }
        launchSingleTop = true
    }
}

private fun NavHostController.navigateToHistoricalTab(
    tab: HistoricalTripBottomBarTab,
    shiftUuid: String,
    currentRoute: String?
) {
    if (currentHistoricalBottomBarTab(currentRoute) == tab) return

    val route = when (tab) {
        HistoricalTripBottomBarTab.STATISTICS -> Routes.historyStatistics(shiftUuid)
        HistoricalTripBottomBarTab.OPERATIONS -> Routes.historyOperations(shiftUuid)
    }

    navigate(route) {
        popUpTo(Routes.HISTORY_GRAPH) {
            inclusive = false
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
