package com.example.chaika.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chaika.ui.screens.*
import com.example.chaika.ui.screens.product.views.ProductEntryScreen
import com.example.chaika.ui.screens.product.ProductScreen
import com.example.chaika.ui.screens.trip.TripScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.TRIP
    ) {
        composable(route = Routes.TRIP) { TripScreen() }
        composable(route = Routes.PRODUCT_ENTRY) {
            ProductEntryScreen(navController)
        }
        composable(route = Routes.PRODUCT) { ProductScreen() }
        composable(route = Routes.SCREEN_3) { Screen_3() }
        composable(route = Routes.PROFILE) { ProfileScreen() }
    }
}