package com.example.chaika.ui.components.bottomBar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.chaika.R
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.theme.ChaikaTheme

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ChaikaTheme {
        BottomBarBackground(
            modifier = Modifier.shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                ),
                clip = true
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomBarIcon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_train),
                selected = currentRoute == Routes.TRIP,
                onClick = {
                    navController.navigate(Routes.TRIP) {
                        popUpTo(Routes.TRIP) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
            BottomBarIcon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_bag),
                selected = currentRoute == Routes.PRODUCT_ENTRY,
                onClick = {
                    navController.navigate(Routes.PRODUCT_ENTRY) {
                        popUpTo(Routes.PRODUCT_ENTRY) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
            BottomBarIcon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_time),
                selected = currentRoute == Routes.SCREEN_3,
                onClick = {
                    navController.navigate(Routes.SCREEN_3) {
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
                        popUpTo(Routes.PROFILE) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}