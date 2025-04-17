package com.example.chaika.ui.screens.trip.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.chaika.ui.viewModels.TripViewModel

@Composable
fun CurrentTripView(
    viewModel: TripViewModel,
    navController: NavController
) {
    Text("CurrentTrip")
}