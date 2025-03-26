package com.example.chaika.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chaika.ui.screens.ProductScreen
import com.example.chaika.ui.screens.ProfileScreen
import com.example.chaika.ui.screens.Screen_3
import com.example.chaika.ui.screens.TripScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.TRIP
    ) {
        composable(Routes.TRIP) { TripScreen() }
        composable(Routes.PRODUCT) { ProductScreen() }
        composable(Routes.SCREEN_3) { Screen_3() }
        composable(Routes.PROFILE) { ProfileScreen() }
    }
}