package com.example.chaika.ui.screens.trip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.chaika.ui.viewModels.TripViewModel
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaika.ui.screens.trip.views.CurrentTripView
import com.example.chaika.ui.screens.trip.views.FindByNumberView
import com.example.chaika.ui.screens.trip.views.FindByStationView
import com.example.chaika.ui.screens.trip.views.NewTripView
import com.example.chaika.ui.screens.trip.views.SelectCarriageView
import com.example.chaika.ui.viewModels.TripViewModel.ScreenState

@Composable
fun TripScreen(
    viewModel: TripViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is ScreenState.NewTrip -> NewTripView()
        is ScreenState.FindByNumber -> FindByNumberView()
        is ScreenState.FindByStation -> FindByStationView()
        is ScreenState.SelectCarriage -> SelectCarriageView()
        is ScreenState.CurrentTrip -> CurrentTripView()
        is ScreenState.Error -> {}
    }
}