package com.example.chaika.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.chaika.ui.components.bottomBar.BottomBar
import com.example.chaika.ui.navigation.NavGraph
import com.example.chaika.ui.navigation.Screen
import com.example.chaika.ui.theme.ChaikaTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.components.topBar.CircleBackButton

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChaikaTheme {
                val navController = rememberNavController()

                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination
                val currentScreen = Screen.fromRoute(currentDestination?.route)

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(currentScreen.titleResId)) },
                            navigationIcon = {
                                if (currentScreen.showBackButton) {
                                    CircleBackButton(
                                        onClick = { navController.navigateUp() },
                                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = { BottomBar(navController) }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
