package com.chaikasoft.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.chaikasoft.app.ui.screens.auth.LoginScreen
import com.chaikasoft.app.ui.screens.operation.OperationScreen
import com.chaikasoft.app.ui.screens.product.ProductCartView
import com.chaikasoft.app.ui.screens.product.ProductEntryView
import com.chaikasoft.app.ui.screens.product.ProductPackageView
import com.chaikasoft.app.ui.screens.product.ProductReplenishView
import com.chaikasoft.app.ui.screens.product.TemplateDetailView
import com.chaikasoft.app.ui.screens.product.TemplateEditView
import com.chaikasoft.app.ui.screens.product.TemplateSearchView
import com.chaikasoft.app.ui.screens.profile.ProfileScreen
import com.chaikasoft.app.ui.screens.profile.views.AboutView
import com.chaikasoft.app.ui.screens.profile.views.FaqsView
import com.chaikasoft.app.ui.screens.profile.views.FeedbackView
import com.chaikasoft.app.ui.screens.profile.views.PersonalDataView
import com.chaikasoft.app.ui.screens.profile.views.SettingsView
import com.chaikasoft.app.ui.screens.trip.FindByNumberView
import com.chaikasoft.app.ui.screens.trip.SelectCarriageView
import com.chaikasoft.app.ui.screens.trip.AutonomousTripScreen
import com.chaikasoft.app.ui.screens.trip.MainTripView
import com.chaikasoft.app.ui.screens.util.ErrorScreen
import com.chaikasoft.app.ui.screens.util.LoadingScreen
import com.chaikasoft.app.ui.screens.statistics.StatisticsScreen
import com.chaikasoft.app.ui.viewModels.*
import kotlinx.coroutines.flow.StateFlow

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // 1) Слушаем единое состояние авторизации
    val ui by authViewModel.uiState.collectAsStateWithLifecycle()

    // 2) При смене state — переключаем стек на нужный корневой граф
    LaunchedEffect(ui.state) {
        when (ui.state) {
            AuthState.Checking -> {
                navController.navigate(Routes.LOADING) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    launchSingleTop = true
                }
            }
            AuthState.Unauthenticated -> {
                navController.navigate(Routes.AUTH_GRAPH) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    launchSingleTop = true
                }
            }
            AuthState.Authenticated -> {
                navController.navigate(Routes.TRIP_GRAPH) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // 3) Единый NavHost со стабильным стартом LOADING
    NavHost(
        navController = navController,
        startDestination = Routes.LOADING
    ) {
        // --- LOADING (пока идёт проверка токена) ---
        composable(Routes.LOADING) {
            LoadingScreen()
        }

        // --- AUTH GRAPH ---
        navigation(
            startDestination = Routes.LOGIN,
            route = Routes.AUTH_GRAPH
        ) {
            composable(Routes.LOGIN) {
                // ВАЖНО: LoginScreen больше не знает про navController
                LoginScreen(
                    viewModel = authViewModel
                )
            }
        }

        // --- MAIN / TRIP GRAPH ---
        navigation(
            startDestination = Routes.TRIP_MAIN,
            route = Routes.TRIP_GRAPH
        ) {
            composable(Routes.TRIP_MAIN) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.TRIP_GRAPH)
                }
                val tripViewModel = hiltViewModel<TripViewModel>(parentEntry)
                MainTripView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(Routes.TRIP_AUTONOMOUS) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.TRIP_GRAPH)
                }
                val autonomousViewModel = hiltViewModel<AutonomousViewModel>(parentEntry)
                AutonomousTripScreen(
                    viewModel = autonomousViewModel,
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
        }

        // --- PRODUCT GRAPH ---
        navigation(
            startDestination = Routes.PRODUCT_ENTRY,
            route = Routes.PRODUCT_GRAPH
        ) {
            composable(route = Routes.PRODUCT_ENTRY) {
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

        // --- STATISTICS GRAPH ---
        navigation(
            startDestination = Routes.STATISTICS,
            route = Routes.STATISTICS_GRAPH
        ) {
            composable(route = Routes.STATISTICS) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.STATISTICS_GRAPH)
                }
                val statisticsViewModel = hiltViewModel<StatisticsViewModel>(parentEntry)
                StatisticsScreen(viewModel = statisticsViewModel)
            }
        }

        // --- OPERATION GRAPH ---
        navigation(
            startDestination = Routes.OPERATION,
            route = Routes.OPERATION_GRAPH
        ) {
            composable(route = Routes.OPERATION) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.OPERATION_GRAPH)
                }
                val operationViewModel = hiltViewModel<OperationViewModel>(parentEntry)
                OperationScreen(viewModel = operationViewModel)
            }
        }

        // --- PROFILE GRAPH ---
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
                val conductor by conductorViewModel.conductor.collectAsStateWithLifecycle()
                PersonalDataView(conductor = conductor)
            }
            composable(Routes.PROFILE_SETTINGS) { SettingsView() }
            composable(Routes.PROFILE_FAQS) { FaqsView() }
            composable(Routes.PROFILE_FEEDBACK) { FeedbackView() }
            composable(Routes.PROFILE_ABOUT) { AboutView() }
        }

        // --- ERROR ---
        composable(route = Routes.ERROR) { ErrorScreen() }
    }
}
