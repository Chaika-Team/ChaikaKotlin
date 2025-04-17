package com.example.chaika.ui.screens.trip

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

    LaunchedEffect(uiState) {
        when (uiState) {
            is ScreenState.NewTrip -> navController.navigate(Routes.TRIP_NEW)
            is ScreenState.FindByNumber -> navController.navigate(Routes.TRIP_BY_NUMBER)
            is ScreenState.FindByStation -> navController.navigate(Routes.TRIP_BY_STATION)
            is ScreenState.SelectCarriage -> navController.navigate(Routes.TRIP_SELECT_CARRIAGE)
            is ScreenState.CurrentTrip -> navController.navigate(Routes.TRIP_CURRENT)
            is ScreenState.Error -> {}
        }
    }
}