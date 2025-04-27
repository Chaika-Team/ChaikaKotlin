package com.example.chaika.ui.screens.trip

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.TripViewModel
import com.example.chaika.ui.viewModels.TripViewModel.ScreenState

@Composable
fun TripScreen(
    viewModel: TripViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val shiftStatus by viewModel.shiftStatus.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkActiveShift()
    }

    LaunchedEffect(uiState, shiftStatus) {
        if (shiftStatus == null) return@LaunchedEffect
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        val targetRoute = when (uiState) {
            is ScreenState.NewTrip -> Routes.TRIP_NEW
            is ScreenState.FindByNumber -> Routes.TRIP_BY_NUMBER
            is ScreenState.FindByStation -> Routes.TRIP_BY_STATION
            is ScreenState.SelectCarriage -> Routes.TRIP_SELECT_CARRIAGE
            is ScreenState.CurrentTrip -> Routes.TRIP_CURRENT
            is ScreenState.Error -> null
        }

        Log.d("TripScreen", "uiState: $uiState, currentRoute: $currentRoute, targetRoute: $targetRoute")

        if (targetRoute != null && targetRoute != currentRoute) {
            Log.d("TripScreen", "Navigating to $targetRoute")
            navController.navigate(targetRoute) {
                popUpTo(Routes.TRIP_GRAPH) { inclusive = false }
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