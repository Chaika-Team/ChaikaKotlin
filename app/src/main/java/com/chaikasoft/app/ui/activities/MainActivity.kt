package com.chaikasoft.app.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.chaikasoft.app.ui.navigation.Screen
import com.chaikasoft.app.ui.theme.ChaikaTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.chaikasoft.app.ui.components.topBar.MenuItem
import com.chaikasoft.app.ui.viewModels.AuthViewModel
import com.chaikasoft.app.ui.components.topBar.TopBar
import com.chaikasoft.app.ui.navigation.Routes
import com.example.chaika.ui.components.bottomBar.BottomBar
import com.chaikasoft.app.ui.navigation.NavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChaikaTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val currentScreen = Screen.fromRoute(currentRoute)

                val menuItems = when (currentScreen) {
                    is Screen.Package -> listOf(
                        MenuItem.REFILL
                    )
                    is Screen.FindTripByNumber -> listOf(
                        MenuItem.AUTONOMOUS_TRIP
                    )
                    else -> emptyList()
                }

                Scaffold(
                    topBar = {
                        TopBar(
                            currentScreen = currentScreen,
                            currentRoute = currentRoute,
                            navController = navController,
                            menuItems = if (currentScreen.showMenuIcon == true) {
                                menuItems
                            } else emptyList(),
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
                        NavGraph(navController = navController, authViewModel = authViewModel)
                    }
                }
            }
        }
    }
}
