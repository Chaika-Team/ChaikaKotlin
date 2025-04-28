package com.example.chaika.ui.screens.trip.views

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chaika.ui.components.trip.FoundTripCard
import com.example.chaika.ui.viewModels.TripViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.platform.testTag
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.ui.components.trip.SearchTripSurfaceDropdown
import com.example.chaika.ui.savers.stationDomainSaver
import kotlinx.coroutines.delay

@Composable
fun FindByNumberView(
    viewModel: TripViewModel,
    navController: NavController,
) {
    var searchDate by rememberSaveable { mutableStateOf("") }
    var searchStart by rememberSaveable(stateSaver = stationDomainSaver()) {
        mutableStateOf<StationDomain?>(null)
    }
    var searchFinish by rememberSaveable(stateSaver = stationDomainSaver()) {
        mutableStateOf<StationDomain?>(null)
    }
    val foundTrips by viewModel.foundTripsList.collectAsState()

    LaunchedEffect(searchDate, searchStart, searchFinish) {
        delay(500)
        if (searchStart != null && searchFinish != null && searchDate != "") {
            viewModel.getTrips(
                searchDate, searchStart!!.code, searchFinish!!.code
            )
            Log.d("finbynuber", "${searchStart!!.code}, ${searchFinish!!.code}")
            Log.d("finbynuber", foundTrips.toString())
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchTripSurfaceDropdown(
            searchDate = searchDate,
            onSearchDateChange = { newDate -> searchDate = newDate },
            onStartStationChange = { newStation -> searchStart = newStation },
            onFinishStationChange = { newStation -> searchFinish = newStation },
            suggestStations = { query, limit ->
                viewModel.suggestStations(query, limit)
            },
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 6.dp)
                .testTag("foundTripsGrid")      // ← тест-тег для списка
        ) {
            items(foundTrips) { trip ->
                FoundTripCard(
                    modifier = Modifier, tripRecord = trip, onClick = {
                        trip.let { safeTrip ->
                            viewModel.setSelectCarriage(safeTrip)
                            try {
                                navController.navigate(Routes.TRIP_SELECT_CARRIAGE)
                            } catch (e: Exception) {
                                Log.e("Navigation", "Failed to navigate", e)
                            }
                        }
                    })
            }
        }
    }
}