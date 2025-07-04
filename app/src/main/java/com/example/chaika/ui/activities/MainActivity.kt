package com.example.chaika.ui.activities

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
import com.example.chaika.ui.components.bottomBar.BottomBar
import com.example.chaika.ui.navigation.NavGraph
import com.example.chaika.ui.navigation.Screen
import com.example.chaika.ui.theme.ChaikaTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaika.ui.viewModels.AuthViewModel
import com.example.chaika.ui.components.topBar.TopBar

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

                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination
                val currentScreen = Screen.fromRoute(currentDestination?.route)

                Scaffold(
                    topBar = {
                        TopBar(
                            currentScreen = currentScreen,
                            currentRoute = currentRoute,
                            navController = navController
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
