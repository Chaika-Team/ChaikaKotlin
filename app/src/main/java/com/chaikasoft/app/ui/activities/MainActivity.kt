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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chaikasoft.app.ui.components.bottomBar.BottomBar
import com.chaikasoft.app.ui.components.topBar.MenuItem
import com.chaikasoft.app.ui.components.topBar.TopBar
import com.chaikasoft.app.ui.navigation.NavGraph
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.navigation.Screen
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.viewModels.AuthViewModel
import com.chaikasoft.app.ui.viewModels.TripViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChaikaTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val tripViewModel: TripViewModel = hiltViewModel()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val currentScreen = Screen.fromRoute(currentRoute)

                // Управление ориентацией экрана в зависимости от текущего маршрута
                LaunchedEffect(currentRoute) {
                    requestedOrientation = when (currentRoute) {
                        Routes.STATISTICS -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
                        else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
                                    MenuItem.REFILL -> navController.navigate(Routes.PRODUCT_REPLENISH)
                                    MenuItem.AUTONOMOUS_TRIP -> navController.navigate(Routes.TRIP_AUTONOMOUS)
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomBar(
                            navController = navController,
                            currentRoute = currentRoute
                        )
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        NavGraph(
                            navController = navController,
                            authViewModel = authViewModel,
                            tripViewModel = tripViewModel
                        )
                    }
                }
            }
        }
    }
}