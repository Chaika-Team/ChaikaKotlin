package com.example.chaika.ui.components.topBar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    currentRoute: String?,
    menuItems: List<MenuItem> = emptyList(),
    onMenuItemClick: (MenuItem) -> Unit = {}
) {
    if (currentRoute != null && currentRoute in Routes.routesWithoutTopBar) return

    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(currentScreen?.let { stringResource(it.titleResId) } ?: "") },
        navigationIcon = {
            if (currentScreen?.showBackButton == true) {
                CircleBackButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                )
            }
        },
        actions = {
            if (menuItems.isNotEmpty() || currentScreen?.showMenuIcon == true) {
                Box {
                    CircleMenuButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        menuItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(stringResource(item.titleResId)) },
                                onClick = {
                                    onMenuItemClick(item)
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}