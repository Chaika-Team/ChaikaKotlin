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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chaikasoft.app.ui.components.bottombar.BottomBar
import com.chaikasoft.app.ui.components.bottombar.NavigationBlockedBottomSheet
import com.chaikasoft.app.ui.components.topbar.MenuItem
import com.chaikasoft.app.ui.components.topbar.TopBar
import com.chaikasoft.app.ui.navigation.NavGraph
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
                val currentScreen = Screen.fromRoute(currentRoute)
                var showBlockedNavigationSheet by remember { mutableStateOf(false) }

                // Управление ориентацией экрана в зависимости от текущего маршрута
                LaunchedEffect(currentRoute) {
                    requestedOrientation = when (currentRoute) {
                        Routes.STATISTICS -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
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
                        TopBar(
                            currentScreen = currentScreen,
                            currentRoute = currentRoute,
                            navController = navController,
                            menuItems = if (currentScreen.showMenuIcon) menuItems else emptyList(),
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
                    },
                    bottomBar = {
                        BottomBar(
                            navController = navController,
                            currentRoute = currentRoute,
                            hasActiveShift = hasActiveShift,
                            onBlockedNavigation = {
                                showBlockedNavigationSheet = true
                            }
                        )
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
                            currentRoute = currentRoute
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
