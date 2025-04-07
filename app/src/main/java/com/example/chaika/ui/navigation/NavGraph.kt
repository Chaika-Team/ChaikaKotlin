package com.example.chaika.ui.navigation

import ProductScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chaika.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.TRIP
    ) {
        composable(route = Routes.TRIP) { TripScreen() }
        composable(route = Routes.PRODUCT) { ProductScreen() }
        composable(route = Routes.SCREEN_3) { Screen_3() }
        composable(route = Routes.PROFILE) { ProfileScreen() }
    }
}