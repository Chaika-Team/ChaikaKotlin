package com.example.chaika.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.chaika.ui.screens.OperationScreen
import com.example.chaika.ui.screens.profile.ProfileScreen
import com.example.chaika.ui.screens.auth.LoginScreen
import com.example.chaika.ui.screens.product.ProductScreen
import com.example.chaika.ui.screens.product.views.ProductCartView
import com.example.chaika.ui.screens.product.views.ProductPackageView
import com.example.chaika.ui.screens.product.views.ProductEntryView
import com.example.chaika.ui.screens.product.views.ProductListView
import com.example.chaika.ui.screens.profile.views.AboutView
import com.example.chaika.ui.screens.profile.views.FaqsView
import com.example.chaika.ui.screens.profile.views.FeedbackView
import com.example.chaika.ui.screens.profile.views.PersonalDataView
import com.example.chaika.ui.screens.profile.views.SettingsView
import com.example.chaika.ui.screens.trip.TripScreen
import com.example.chaika.ui.screens.trip.views.CurrentTripView
import com.example.chaika.ui.screens.trip.views.FindByNumberView
import com.example.chaika.ui.screens.trip.views.NewTripView
import com.example.chaika.ui.screens.trip.views.SelectCarriageView
import com.example.chaika.ui.viewModels.AuthViewModel
import com.example.chaika.ui.viewModels.ProductViewModel
import com.example.chaika.ui.viewModels.ProfileViewModel
import com.example.chaika.ui.viewModels.TripViewModel

@Composable
fun NavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) { _ ->
            LoginScreen(
                navController = navController,
                viewModel = authViewModel
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
                ProductEntryView(navController = navController)
            }

            composable(route = Routes.PRODUCT_LIST) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val productViewModel = hiltViewModel<ProductViewModel>(parentEntry)
                ProductListView(viewModel = productViewModel, navController = navController)
            }

            composable(route = Routes.PRODUCT_CART) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val productViewModel = hiltViewModel<ProductViewModel>(parentEntry)
                ProductCartView(viewModel = productViewModel)
            }

            composable(route = Routes.PRODUCT_PACKAGE) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val productViewModel = hiltViewModel<ProductViewModel>(parentEntry)
                ProductPackageView(viewModel = productViewModel, navController = navController)
            }
        }

        composable(route = Routes.OPERATION) {
            OperationScreen()
        }

        navigation(
            startDestination = Routes.PROFILE,
            route = Routes.PROFILE_GRAPH
        ) {
            composable(route = Routes.PROFILE) { backStackEntry ->
                val profileViewModel = hiltViewModel<ProfileViewModel>(backStackEntry)
                ProfileScreen(
                    viewModel = profileViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }
            composable(route = Routes.PROFILE_PERSONAL_DATA) { backStackEntry ->
                val profileViewModel = hiltViewModel<ProfileViewModel>(backStackEntry)
                val conductor = profileViewModel.conductorState.collectAsState().value
                PersonalDataView(conductor = conductor)
            }
            composable(route = Routes.PROFILE_SETTINGS) {
                SettingsView()
            }
            composable(route = Routes.PROFILE_FAQS) {
                FaqsView()
            }
            composable(route = Routes.PROFILE_FEEDBACK) {
                FeedbackView()
            }
            composable(route = Routes.PROFILE_ABOUT) {
                AboutView()
            }
        }
    }
}