package com.chaikasoft.app.ui.components.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentScreen: Screen?,
    modifier: Modifier = Modifier,
    menuItems: List<MenuItem> = emptyList(),
    onMenuItemClick: (MenuItem) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = { Text(currentScreen?.let { stringResource(it.titleResId) } ?: "") },
        navigationIcon = {
            if (currentScreen?.showBackButton == true) {
                CircleBackButton(
                    onClick = onBackClick,
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
