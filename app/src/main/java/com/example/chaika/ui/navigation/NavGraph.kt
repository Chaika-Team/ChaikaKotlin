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
import com.example.chaika.ui.screens.auth.LoginScreen
import com.example.chaika.ui.screens.product.ProductScreen
import com.example.chaika.ui.screens.product.views.CartScreen
import com.example.chaika.ui.screens.product.views.ProductEntryScreen
import com.example.chaika.ui.screens.product.views.ProductListScreen
import com.example.chaika.ui.screens.trip.TripScreen
import com.example.chaika.ui.screens.trip.views.CurrentTripView
import com.example.chaika.ui.screens.trip.views.FindByNumberView
import com.example.chaika.ui.screens.trip.views.NewTripView
import com.example.chaika.ui.screens.trip.views.SelectCarriageView
import com.example.chaika.ui.viewModels.AuthViewModel
import com.example.chaika.ui.viewModels.ProductViewModel
import com.example.chaika.ui.viewModels.TripViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            val viewModel = hiltViewModel<AuthViewModel>()
            LoginScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

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

        navigation(
            startDestination = Routes.PRODUCT,
            route = Routes.PRODUCT_GRAPH
        ) {
            composable(route = Routes.PRODUCT) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val productViewModel = hiltViewModel<ProductViewModel>(parentEntry)
                ProductScreen(viewModel = productViewModel, navController = navController)
            }

            composable(route = Routes.PRODUCT_ENTRY) { _ ->
                ProductEntryScreen(navController = navController)
            }

            composable(route = Routes.PRODUCT_LIST) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val productViewModel = hiltViewModel<ProductViewModel>(parentEntry)
                ProductListScreen(viewModel = productViewModel, navController = navController)
            }

            composable(route = Routes.PRODUCT_CART) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val productViewModel = hiltViewModel<ProductViewModel>(parentEntry)
                CartScreen(viewModel = productViewModel)
            }
        }

        composable(route = Routes.OPERATION) {
            OperationScreen()
        }

        composable(route = Routes.PROFILE) {
            ProfileScreen()
        }
    }
}