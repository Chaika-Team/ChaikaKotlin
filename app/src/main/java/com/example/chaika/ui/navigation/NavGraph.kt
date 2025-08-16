package com.example.chaika.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.chaika.ui.screens.OperationScreen
import com.example.chaika.ui.screens.profile.ProfileScreen
import com.example.chaika.ui.screens.auth.LoginScreen
import com.example.chaika.ui.screens.product.ProductCartView
import com.example.chaika.ui.screens.product.ProductEntryView
import com.example.chaika.ui.screens.product.ProductPackageView
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
import com.example.chaika.ui.viewModels.TemplateViewModel
import androidx.compose.runtime.*
import com.example.chaika.ui.screens.product.ProductReplenishView
import com.example.chaika.ui.screens.product.TemplateDetailView
import com.example.chaika.ui.screens.product.TemplateEditView
import com.example.chaika.ui.screens.product.TemplateSearchView
import com.example.chaika.ui.screens.util.ErrorScreen
import com.example.chaika.ui.screens.util.LoadingScreen
import com.example.chaika.ui.viewModels.ConductorViewModel
import com.example.chaika.ui.viewModels.FillViewModel
import com.example.chaika.ui.viewModels.PackageViewModel
import com.example.chaika.ui.viewModels.ReplenishViewModel
import com.example.chaika.ui.viewModels.SaleViewModel

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
            startDestination = Routes.PRODUCT_ENTRY,
            route = Routes.PRODUCT_GRAPH
        ) {
            composable(route = Routes.PRODUCT_ENTRY) { _ ->
                ProductEntryView(navController = navController)
            }

            composable(route = Routes.PRODUCT_CART) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val saleViewModel = hiltViewModel<SaleViewModel>(parentEntry)
                val conductorViewModel = hiltViewModel<ConductorViewModel>(parentEntry)
                ProductCartView(
                    saleViewModel = saleViewModel,
                    conductorViewModel = conductorViewModel
                )
            }

            composable(route = Routes.PRODUCT_PACKAGE) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val packageViewModel = hiltViewModel<PackageViewModel>(parentEntry)
                val saleViewModel = hiltViewModel<SaleViewModel>(parentEntry)
                ProductPackageView(
                    packageViewModel = packageViewModel,
                    saleViewModel = saleViewModel,
                    navController = navController
                )
            }

            composable(route = Routes.PRODUCT_REPLENISH) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val productViewModel = hiltViewModel<ProductViewModel>(parentEntry)
                val replenishViewModel = hiltViewModel<ReplenishViewModel>(parentEntry)
                val conductorViewModel = hiltViewModel<ConductorViewModel>(parentEntry)
                ProductReplenishView(
                    productViewModel = productViewModel,
                    replenishViewModel = replenishViewModel,
                    conductorViewModel = conductorViewModel,
                    navController = navController
                )
            }

            composable(route = Routes.TEMPLATE_SEARCH) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val templateViewModel = hiltViewModel<TemplateViewModel>(parentEntry)
                TemplateSearchView(viewModel = templateViewModel, navController = navController)
            }

            composable(route = Routes.TEMPLATE_DETAIL) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val templateViewModel = hiltViewModel<TemplateViewModel>(parentEntry)
                val fillViewModel = hiltViewModel<FillViewModel>(parentEntry)
                val productViewModel = hiltViewModel<ProductViewModel>(parentEntry)
                val templateId = backStackEntry.arguments?.getString("templateId")?.toIntOrNull()
                if (templateId != null) {
                    TemplateDetailView(
                        templateId = templateId,
                        viewModel = templateViewModel,
                        fillViewModel = fillViewModel,
                        productViewModel = productViewModel,
                        navController = navController
                    )
                }
            }

            composable(route = Routes.TEMPLATE_EDIT) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val productViewModel = hiltViewModel<ProductViewModel>(parentEntry)
                val fillViewModel = hiltViewModel<FillViewModel>(parentEntry)
                val conductorViewModel = hiltViewModel<ConductorViewModel>(parentEntry)
                TemplateEditView(
                    productViewModel = productViewModel,
                    fillViewModel = fillViewModel,
                    conductorViewModel = conductorViewModel,
                    navController = navController
                )
            }
        }

        composable(route = Routes.OPERATION) {
            OperationScreen()
        }

        navigation(
            startDestination = Routes.PROFILE,
            route = Routes.PROFILE_GRAPH
        ) {
            composable(Routes.PROFILE) { backStackEntry ->
                val profileViewModel = hiltViewModel<ProfileViewModel>(backStackEntry)
                ProfileScreen(
                    viewModel = profileViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }
            composable(Routes.PROFILE_PERSONAL_DATA) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PROFILE_GRAPH)
                }
                val conductorViewModel = hiltViewModel<ConductorViewModel>(parentEntry)
                val conductor by conductorViewModel.conductor.collectAsState()
                PersonalDataView(conductor = conductor)
            }
            composable(Routes.PROFILE_SETTINGS) { _ ->
                SettingsView()
            }
            composable(Routes.PROFILE_FAQS) { _ ->
                FaqsView()
            }
            composable(Routes.PROFILE_FEEDBACK) { _ ->
                FeedbackView()
            }
            composable(Routes.PROFILE_ABOUT) { _ ->
                AboutView()
            }
        }

        composable(route = Routes.ERROR) { backStackEntry ->
            ErrorScreen()
        }

        composable(route = Routes.LOADING) { backStackEntry ->
            LoadingScreen()
        }
    }
}