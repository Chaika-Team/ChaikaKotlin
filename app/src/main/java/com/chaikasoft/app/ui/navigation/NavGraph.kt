package com.chaikasoft.app.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.chaikasoft.app.ui.screens.operation.OperationScreen
import com.chaikasoft.app.ui.screens.profile.ProfileScreen
import com.chaikasoft.app.ui.screens.auth.LoginScreen
import com.chaikasoft.app.ui.screens.product.ProductCartView
import com.chaikasoft.app.ui.screens.product.ProductEntryView
import com.chaikasoft.app.ui.screens.product.ProductPackageView
import com.chaikasoft.app.ui.screens.profile.views.AboutView
import com.chaikasoft.app.ui.screens.profile.views.FaqsView
import com.chaikasoft.app.ui.screens.profile.views.FeedbackView
import com.chaikasoft.app.ui.screens.profile.views.PersonalDataView
import com.chaikasoft.app.ui.screens.profile.views.SettingsView
import com.chaikasoft.app.ui.screens.trip.TripScreen
import com.chaikasoft.app.ui.screens.trip.views.CurrentTripView
import com.chaikasoft.app.ui.screens.trip.views.FindByNumberView
import com.chaikasoft.app.ui.screens.trip.views.NewTripView
import com.chaikasoft.app.ui.screens.trip.views.SelectCarriageView
import com.chaikasoft.app.ui.viewModels.AuthViewModel
import com.chaikasoft.app.ui.viewModels.ProductViewModel
import com.chaikasoft.app.ui.viewModels.TripViewModel
import com.chaikasoft.app.ui.viewModels.TemplateViewModel
import androidx.compose.runtime.*
import com.chaikasoft.app.ui.screens.product.ProductReplenishView
import com.chaikasoft.app.ui.screens.product.TemplateDetailView
import com.chaikasoft.app.ui.screens.product.TemplateEditView
import com.chaikasoft.app.ui.screens.product.TemplateSearchView
import com.chaikasoft.app.ui.screens.util.ErrorScreen
import com.chaikasoft.app.ui.screens.util.LoadingScreen
import com.chaikasoft.app.ui.viewModels.ConductorViewModel
import com.chaikasoft.app.ui.viewModels.FillViewModel
import com.chaikasoft.app.ui.viewModels.PackageViewModel
import com.chaikasoft.app.ui.viewModels.ReplenishItemsViewModel
import com.chaikasoft.app.ui.viewModels.ReplenishViewModel
import com.chaikasoft.app.ui.viewModels.SaleViewModel

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
                val replenishViewModel = hiltViewModel<ReplenishViewModel>(parentEntry)
                val replenishItemsViewModel = hiltViewModel<ReplenishItemsViewModel>(parentEntry)
                val conductorViewModel = hiltViewModel<ConductorViewModel>(parentEntry)
                ProductReplenishView(
                    replenishViewModel = replenishViewModel,
                    conductorViewModel = conductorViewModel,
                    replenishItemsViewModel = replenishItemsViewModel,
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
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PROFILE_GRAPH)
                }
                val conductorViewModel = hiltViewModel<ConductorViewModel>(parentEntry)
                ProfileScreen(
                    conductorViewModel = conductorViewModel,
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