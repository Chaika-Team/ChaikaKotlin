package com.example.chaika.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
            composable(Routes.TRIP) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.TRIP_GRAPH)
                }
                val tripViewModel = hiltViewModel<TripViewModel>(parentEntry)
                TripScreen(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(Routes.TRIP_NEW) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.TRIP_GRAPH)
                }
                val tripViewModel = hiltViewModel<TripViewModel>(parentEntry)
                NewTripView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(Routes.TRIP_BY_NUMBER) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.TRIP_GRAPH)
                }
                val tripViewModel = hiltViewModel<TripViewModel>(parentEntry)
                FindByNumberView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(Routes.TRIP_SELECT_CARRIAGE) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.TRIP_GRAPH)
                }
                val tripViewModel = hiltViewModel<TripViewModel>(parentEntry)
                SelectCarriageView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(route = Routes.TRIP_CURRENT) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.TRIP_GRAPH)
                }
                val tripViewModel = hiltViewModel<TripViewModel>(parentEntry)
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