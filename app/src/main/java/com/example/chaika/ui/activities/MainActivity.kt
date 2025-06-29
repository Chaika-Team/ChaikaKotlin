package com.example.chaika.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.chaika.ui.components.bottomBar.BottomBar
import com.example.chaika.ui.navigation.NavGraph
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.theme.ChaikaTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaika.ui.viewModels.AuthViewModel

@Composable
fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return currentRoute != null && currentRoute !in Routes.routesWithoutBottomBar
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChaikaTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                Scaffold(
                    bottomBar = {
                        if (shouldShowBottomBar(currentRoute)) {
                            BottomBar(navController)
                        }
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
