package com.example.chaika.ui.screens.product

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.ProductViewModel
import com.example.chaika.ui.viewModels.ProductViewModel.ScreenState

@Composable
fun ProductScreen(
    viewModel: ProductViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        val targetRoute = when (uiState) {
            is ScreenState.ProductList -> Routes.PRODUCT_LIST
            is ScreenState.Empty -> Routes.PRODUCT_ENTRY
            is ScreenState.Cart -> Routes.PRODUCT_CART
            is ScreenState.Package -> Routes.PRODUCT_PACKAGE
            is ScreenState.Error -> null
        }

        Log.d("ProductScreen", "uiState: $uiState, currentRoute: $currentRoute, targetRoute: $targetRoute")

        if (targetRoute != null && targetRoute != currentRoute) {
            Log.d("TripScreen", "Navigating to $targetRoute")
            navController.navigate(targetRoute) {
                popUpTo(Routes.PRODUCT_GRAPH) { inclusive = false }
                launchSingleTop = true
                restoreState = true
            }
        }

        if (uiState is ScreenState.Error) {
            // TODO()
            Log.e("TripScreen", "Some error")
        }
    }
}