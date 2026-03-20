package com.chaikasoft.app.ui.screens.trip

import android.util.Log
import androidx.compose.foundation.layout.Box
import com.chaikasoft.app.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaikasoft.app.ui.components.trip.foundTripCard
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.components.trip.searchTripSurfaceDropdown
import com.chaikasoft.app.ui.state.TripsSearchUiState
import com.chaikasoft.app.ui.viewModels.TripViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(FlowPreview::class)
@Composable
fun findByNumberView(
    viewModel: TripViewModel,
    navController: NavController,
) {
    val searchDate by viewModel.searchDate.collectAsStateWithLifecycle()
    val fromQuery by viewModel.fromQuery.collectAsStateWithLifecycle()
    val toQuery by viewModel.toQuery.collectAsStateWithLifecycle()
    val searchStart by viewModel.searchStartStation.collectAsStateWithLifecycle()
    val searchFinish by viewModel.searchFinishStation.collectAsStateWithLifecycle()

    val tripsState by viewModel.tripsSearchState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onFindByNumberScreenShown()
    }

    LaunchedEffect(Unit) {
        snapshotFlow { Triple(searchDate, searchStart?.code, searchFinish?.code) }
            .debounce(500)
            .distinctUntilChanged()
            .collectLatest { (date, from, to) ->
                if (from != null && to != null && date.isNotBlank()) {
                    viewModel.getTrips(date, from, to)
                    Log.d("FindByNumberView", "Search params: date=$date from=$from to=$to")
                } else {
                    viewModel.resetTripsSearchResults()
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        searchTripSurfaceDropdown(
            searchDate = searchDate,
            onSearchDateChange = { viewModel.onSearchDateChanged(it) },

            fromQuery = fromQuery,
            onFromQueryChange = { q ->
                viewModel.onFromQueryChanged(q)
                if (searchStart != null && q != searchStart!!.name) {
                    viewModel.onStartStationChanged(null)
                }
            },

            toQuery = toQuery,
            onToQueryChange = { q ->
                viewModel.onToQueryChanged(q)
                if (searchFinish != null && q != searchFinish!!.name) {
                    viewModel.onFinishStationChanged(null)
                }
            },

            fromSuggestions = viewModel.fromSuggestions,
            toSuggestions = viewModel.toSuggestions,

            onStartStationChange = { station ->
                viewModel.onStartStationChanged(station)
            },
            onFinishStationChange = { station ->
                viewModel.onFinishStationChanged(station)
            },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val s = tripsState) {
                TripsSearchUiState.Idle -> {
                    // Можно показать подсказку: "Выберите станции и дату"
                }

                TripsSearchUiState.Loading -> {
                    CircularProgressIndicator()
                }

                TripsSearchUiState.Empty -> {
                    Text(stringResource(R.string.trip_search_empty))
                }

                is TripsSearchUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(s.messageRes))
                        if (s.retryable) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.retryTrips()
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .height(46.dp)
                                    .width(200.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(fontSize = 18.sp, text = stringResource(R.string.retry))
                            }
                        }
                    }
                }

                is TripsSearchUiState.Content -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(s.trips) { trip ->
                            foundTripCard(
                                modifier = Modifier,
                                tripRecord = trip,
                                onClick = {
                                    viewModel.preserveSearchForBackNavigation()
                                    viewModel.selectTrip(trip)

                                    try {
                                        navController.navigate(Routes.TRIP_SELECT_CARRIAGE)
                                    } catch (e: IllegalStateException) {
                                        Log.e("Navigation", "Failed to navigate", e)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
