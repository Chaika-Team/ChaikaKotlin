package com.example.chaika.ui.components.bottomBa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chaika.R
import com.example.chaika.ui.components.bottomBar.BottomBarBox
import com.example.chaika.ui.components.bottomBar.BottomBarIcon
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.theme.ProductTheme

@Composable
fun BottomBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Surface(
        color = Color.Transparent, // Make sure parent doesn't interfere
        modifier = Modifier.shadow(
            elevation = 10.dp,
            shape = RoundedCornerShape(16.dp),
            clip = true
        )
    ) {
        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp)),
            containerColor = Color.White.copy(alpha = 1f), // Force opaque white
            contentColor = LocalContentColor.current,
            tonalElevation = 0.dp, // Disable Material's automatic elevation colors
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BottomBarIcon(
                    iconRes = R.drawable.ic_train,
                    selected = currentRoute == Routes.TRIP,
                    onClick = { navController.navigate(Routes.TRIP) }
                )
                BottomBarIcon(
                    iconRes = R.drawable.ic_bag,
                    selected = currentRoute == Routes.PRODUCT,
                    onClick = { navController.navigate(Routes.PRODUCT) }
                )
                BottomBarIcon(
                    iconRes = R.drawable.ic_time,
                    selected = currentRoute == Routes.SCREEN_3,
                    onClick = { navController.navigate(Routes.SCREEN_3) }
                )
                BottomBarIcon(
                    iconRes = R.drawable.ic_profile_bar,
                    selected = currentRoute == Routes.PROFILE,
                    onClick = { navController.navigate(Routes.PROFILE) }
                )
            }
        }
    }
}