package com.example.chaika.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.chaika.ui.screens.OperationScreen
import com.example.chaika.ui.screens.ProfileScreen
import com.example.chaika.ui.screens.product.ProductScreen
import com.example.chaika.ui.screens.product.views.ProductEntryScreen
import com.example.chaika.ui.screens.trip.TripScreen
import com.example.chaika.ui.screens.trip.views.CurrentTripView
import com.example.chaika.ui.screens.trip.views.FindByNumberView
import com.example.chaika.ui.screens.trip.views.FindByStationView
import com.example.chaika.ui.screens.trip.views.NewTripView
import com.example.chaika.ui.screens.trip.views.SelectCarriageView
import com.example.chaika.ui.viewModels.ProductViewModel
import com.example.chaika.ui.viewModels.TripViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    val productViewModel: ProductViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.TRIP_GRAPH
    ) {

        navigation(
            startDestination = Routes.TRIP,
            route = Routes.TRIP_GRAPH
        ) {
            composable(route = Routes.TRIP) { backStackEntry ->
                val tripViewModel = hiltViewModel<TripViewModel>(backStackEntry)
                TripScreen(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(route = Routes.TRIP_NEW) { backStackEntry ->
                val tripViewModel = hiltViewModel<TripViewModel>(backStackEntry)
                NewTripView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(route = Routes.TRIP_BY_NUMBER) { backStackEntry ->
                val tripViewModel = hiltViewModel<TripViewModel>(backStackEntry)
                FindByNumberView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(route = Routes.TRIP_BY_STATION) { backStackEntry ->
                val tripViewModel = hiltViewModel<TripViewModel>(backStackEntry)
                FindByStationView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(route = Routes.TRIP_SELECT_CARRIAGE) { backStackEntry ->
                val tripViewModel = hiltViewModel<TripViewModel>(backStackEntry)
                SelectCarriageView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(route = Routes.TRIP_CURRENT) { backStackEntry ->
                val tripViewModel = hiltViewModel<TripViewModel>(backStackEntry)
                CurrentTripView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }
        }

        composable(route = Routes.PRODUCT_ENTRY) {
            ProductEntryScreen(navController = navController)
        }
        composable(route = Routes.PRODUCT) {
            ProductScreen(viewModel = productViewModel, navController = navController)
        }
        composable(route = Routes.OPERATION) {
            OperationScreen(navController = navController)
        }
        composable(route = Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }
    }
}