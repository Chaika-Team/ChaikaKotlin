package com.example.chaika.ui.components.bottomBa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.chaika.R
import com.example.chaika.ui.components.bottomBar.BottomBarIcon
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.theme.ChaikaTheme

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ChaikaTheme {
        val shapes = MaterialTheme.shapes
        Surface(
            color = Color.Transparent,
            modifier = Modifier.shadow(
                elevation = 10.dp,
                shape = shapes.large,
                clip = true
            )
        ) {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.extraLarge),
                containerColor = Color.White.copy(alpha = 1f),
                contentColor = LocalContentColor.current,
                tonalElevation = 0.dp,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BottomBarIcon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_train),
                        selected = currentRoute == Routes.TRIP,
                        onClick = {
                            navController.navigate(Routes.TRIP) {
                                // Очищаем стек до TRIP (включительно) и создаем новый экземпляр
                                popUpTo(Routes.TRIP) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    BottomBarIcon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_bag),
                        selected = currentRoute == Routes.PRODUCT,
                        onClick = {
                            navController.navigate(Routes.PRODUCT) {
                                // Очищаем стек до PRODUCT (включительно) и создаем новый экземпляр
                                popUpTo(Routes.PRODUCT) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    BottomBarIcon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_time),
                        selected = currentRoute == Routes.SCREEN_3,
                        onClick = {
                            navController.navigate(Routes.SCREEN_3) {
                                // Очищаем стек до SCREEN_3 (включительно) и создаем новый экземпляр
                                popUpTo(Routes.SCREEN_3) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    BottomBarIcon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_profile_bar),
                        selected = currentRoute == Routes.PROFILE,
                        onClick = {
                            navController.navigate(Routes.PROFILE) {
                                // Очищаем стек до PROFILE (включительно) и создаем новый экземпляр
                                popUpTo(Routes.PROFILE) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}