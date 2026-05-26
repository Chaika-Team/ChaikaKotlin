package com.chaikasoft.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
import com.chaikasoft.app.ui.screens.product.TemplateConfirmView
import com.chaikasoft.app.ui.screens.product.TemplateDetailView
import com.chaikasoft.app.ui.screens.product.TemplateEditView
import com.chaikasoft.app.ui.screens.product.TemplateSearchView
import com.chaikasoft.app.ui.screens.profile.AboutView
import com.chaikasoft.app.ui.screens.profile.FaqsView
import com.chaikasoft.app.ui.screens.profile.FeedbackView
import com.chaikasoft.app.ui.screens.profile.MainProfileView
import com.chaikasoft.app.ui.screens.profile.PersonalDataView
import com.chaikasoft.app.ui.screens.profile.SettingsView
import com.chaikasoft.app.ui.screens.statistics.StatisticsScreen
import com.chaikasoft.app.ui.screens.trip.AutonomousTripScreen
import com.chaikasoft.app.ui.screens.trip.FindByNumberView
import com.chaikasoft.app.ui.screens.trip.MainTripView
import com.chaikasoft.app.ui.screens.trip.SelectCarriageView
import com.chaikasoft.app.ui.screens.util.ErrorScreen
import com.chaikasoft.app.ui.screens.util.LoadingScreen
import com.chaikasoft.app.ui.viewmodels.AuthState
import com.chaikasoft.app.ui.viewmodels.AuthViewModel
import com.chaikasoft.app.ui.viewmodels.AutonomousViewModel
import com.chaikasoft.app.ui.viewmodels.ConductorViewModel
import com.chaikasoft.app.ui.viewmodels.FillViewModel
import com.chaikasoft.app.ui.viewmodels.OperationViewModel
import com.chaikasoft.app.ui.viewmodels.PackageViewModel
import com.chaikasoft.app.ui.viewmodels.PostAuthGateViewModel
import com.chaikasoft.app.ui.viewmodels.ProductGateViewModel
import com.chaikasoft.app.ui.viewmodels.ProductViewModel
import com.chaikasoft.app.ui.viewmodels.ReplenishItemsViewModel
import com.chaikasoft.app.ui.viewmodels.ReplenishViewModel
import com.chaikasoft.app.ui.viewmodels.SaleViewModel
import com.chaikasoft.app.ui.viewmodels.StatisticsViewModel
import com.chaikasoft.app.ui.viewmodels.TemplateViewModel
import com.chaikasoft.app.ui.viewmodels.TripViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    tripViewModel: TripViewModel,
    hasActiveShift: Boolean,
    currentRoute: String?
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

    LaunchedEffect(ui.state, hasActiveShift, currentRoute) {
        if (ui.state != AuthState.Authenticated) {
            return@LaunchedEffect
        }

        if (hasActiveShift || !NavigationGuards.requiresActiveShift(currentRoute)) {
            return@LaunchedEffect
        }

        val protectedGraph = NavigationGuards.protectedGraphForRoute(currentRoute)
        navController.navigate(Routes.TRIP_MAIN) {
            if (protectedGraph != null) {
                popUpTo(protectedGraph) { inclusive = true }
            }
            launchSingleTop = true
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
            startDestination = Routes.TRIP_GATE,
            route = Routes.TRIP_GRAPH
        ) {
            composable(Routes.TRIP_GATE) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.TRIP_GRAPH)
                }
                val gateVm = hiltViewModel<PostAuthGateViewModel>(parentEntry)
                val gateState by gateVm.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    gateVm.prepare()
                }

                LaunchedEffect(gateState) {
                    if (gateState is PostAuthGateViewModel.PostAuthGateUiState.Ready) {
                        navController.navigate(Routes.TRIP_MAIN) {
                            popUpTo(Routes.TRIP_GATE) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                val currentGateState = gateState
                val stateTag = when (currentGateState) {
                    PostAuthGateViewModel.PostAuthGateUiState.Loading -> "tripGateStateLoading"
                    is PostAuthGateViewModel.PostAuthGateUiState.Ready -> {
                        if (currentGateState.hadRefreshFailure) {
                            "tripGateStateReadyWithRefreshFailure"
                        } else {
                            "tripGateStateReady"
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("tripGateScreen")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(stateTag)
                    ) {
                        LoadingScreen()
                    }
                }
            }

            composable(Routes.TRIP_MAIN) {
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

            composable(Routes.TRIP_BY_NUMBER) {
                FindByNumberView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }

            composable(Routes.TRIP_SELECT_CARRIAGE) {
                SelectCarriageView(
                    viewModel = tripViewModel,
                    navController = navController
                )
            }
        }

        // --- PRODUCT GRAPH ---
        navigation(
            startDestination = Routes.PRODUCT_GATE,
            route = Routes.PRODUCT_GRAPH
        ) {
            composable(Routes.PRODUCT_GATE) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val gateVm = hiltViewModel<ProductGateViewModel>(parentEntry)
                val gateState by gateVm.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    gateVm.resolveTarget()
                }

                LaunchedEffect(gateState) {
                    val target = (gateState as? ProductGateViewModel.ProductGateUiState.Resolved)
                        ?.target ?: return@LaunchedEffect
                    when (target) {
                        ProductGateViewModel.Target.PACKAGE ->
                            navController.navigate(Routes.PRODUCT_PACKAGE) {
                                popUpTo(Routes.PRODUCT_GATE) { inclusive = true }
                                launchSingleTop = true
                            }

                        ProductGateViewModel.Target.ENTRY ->
                            navController.navigate(Routes.PRODUCT_ENTRY) {
                                popUpTo(Routes.PRODUCT_GATE) { inclusive = true }
                                launchSingleTop = true
                            }
                    }
                }

                LoadingScreen()
            }
            composable(route = Routes.PRODUCT_ENTRY) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val productViewModel = hiltViewModel<ProductViewModel>(parentEntry)
                val fillViewModel = hiltViewModel<FillViewModel>(parentEntry)
                ProductEntryView(
                    navController = navController,
                    productViewModel = productViewModel,
                    fillViewModel = fillViewModel
                )
            }

            composable(route = Routes.PRODUCT_CART) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.PRODUCT_GRAPH)
                }
                val saleViewModel = hiltViewModel<SaleViewModel>(parentEntry)
                val conductorViewModel = hiltViewModel<ConductorViewModel>(parentEntry)
                ProductCartView(
                    saleViewModel = saleViewModel,
                    conductorViewModel = conductorViewModel,
                    navController = navController
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
                val packageViewModel = hiltViewModel<PackageViewModel>(parentEntry)
                ProductReplenishView(
                    replenishViewModel = replenishViewModel,
                    conductorViewModel = conductorViewModel,
                    replenishItemsViewModel = replenishItemsViewModel,
                    packageViewModel = packageViewModel,
                    navController = navController
                )
            }

            navigation(
                startDestination = Routes.TEMPLATE_SEARCH,
                route = Routes.TEMPLATE_GRAPH
            ) {
                composable(Routes.TEMPLATE_SEARCH) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Routes.TEMPLATE_GRAPH)
                    }
                    val templateViewModel = hiltViewModel<TemplateViewModel>(parentEntry)

                    TemplateSearchView(
                        viewModel = templateViewModel,
                        navController = navController
                    )
                }

                composable(Routes.TEMPLATE_DETAIL) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Routes.TEMPLATE_GRAPH)
                    }

                    val templateViewModel = hiltViewModel<TemplateViewModel>(parentEntry)
                    val fillViewModel = hiltViewModel<FillViewModel>(parentEntry)

                    val templateId = backStackEntry.arguments
                        ?.getString("templateId")
                        ?.toIntOrNull() ?: return@composable

                    TemplateDetailView(
                        templateId = templateId,
                        viewModel = templateViewModel,
                        fillViewModel = fillViewModel,
                        navController = navController
                    )
                }

                composable(Routes.TEMPLATE_EDIT) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Routes.TEMPLATE_GRAPH)
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

                composable(Routes.TEMPLATE_CONFIRM) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Routes.TEMPLATE_GRAPH)
                    }

                    val fillViewModel = hiltViewModel<FillViewModel>(parentEntry)
                    val conductorViewModel = hiltViewModel<ConductorViewModel>(parentEntry)

                    TemplateConfirmView(
                        fillViewModel = fillViewModel,
                        conductorViewModel = conductorViewModel,
                        navController = navController
                    )
                }
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
                MainProfileView(
                    conductorViewModel = conductorViewModel,
                    authViewModel = authViewModel,
                    navController = navController,
                    tripViewModel = tripViewModel
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
