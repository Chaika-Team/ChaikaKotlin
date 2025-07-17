package com.example.chaika.ui.components.topBar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentScreen: Screen?,
    navController: NavController,
    currentRoute: String?
) {
    if (currentRoute != null && currentRoute in Routes.routesWithoutTopBar) return

    TopAppBar(
        title = { Text(currentScreen?.let { stringResource(it.titleResId) } ?: "") },
        navigationIcon = {
            if (currentScreen?.showBackButton == true) {
                CircleBackButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                )
            }
        }
    )
}