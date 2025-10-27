package com.chaikasoft.app.ui.screens.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaikasoft.app.ui.components.trip.CurrentTripCard
import com.chaikasoft.app.ui.components.trip.FinishTripResultBottomSheet
import com.chaikasoft.app.ui.components.trip.HistoryRecordCard
import com.chaikasoft.app.ui.components.trip.HistoryToNowDivider
import com.chaikasoft.app.ui.components.trip.NewTripButton
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.viewModels.TripViewModel

@Composable
fun MainTripView(
    viewModel: TripViewModel,
    navController: NavController,
) {
    val history by viewModel.pagingHistoryFlow.collectAsStateWithLifecycle()
    val selectedTrip by viewModel.selectedTripRecord.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
        viewModel.checkActiveShift()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 6.dp)
        ) {
            items(history) { tripRecord ->
                HistoryRecordCard(
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                    tripRecord = tripRecord
                )
            }
        }

        HistoryToNowDivider()

        if (selectedTrip != null) {
            CurrentTripCard(
                tripRecord = selectedTrip!!,
                onClick = {
                    viewModel.finishCurrentTrip()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 16.dp)
            )
        } else {
            NewTripButton(
                onClick = {
                    navController.navigate(Routes.TRIP_BY_NUMBER)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 16.dp)
            )
        }

        FinishTripResultBottomSheet(
            tripViewModel = viewModel,
            onDismissWithLogout = { }
        )
    }
}