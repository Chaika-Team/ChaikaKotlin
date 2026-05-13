package com.chaikasoft.app.ui.screens.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaikasoft.app.ui.components.trip.CurrentTripCard
import com.chaikasoft.app.ui.components.trip.FinishTripResultBottomSheet
import com.chaikasoft.app.ui.components.trip.HistoryRecordCard
import com.chaikasoft.app.ui.components.trip.HistoryToNowDivider
import com.chaikasoft.app.ui.components.trip.NewTripButton
import com.chaikasoft.app.ui.components.trip.RetrySendConfirmBottomSheet
import com.chaikasoft.app.ui.components.trip.RetrySendResultBottomSheet
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.viewmodels.TripViewModel

@Composable
fun MainTripView(viewModel: TripViewModel, navController: NavController) {
    val history by viewModel.shiftHistory.collectAsStateWithLifecycle()
    val selectedTrip by viewModel.selectedTripRecord.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
        viewModel.checkActiveShift()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopHistoryObserving() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tripMainScreen")
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            reverseLayout = true,
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 6.dp)
        ) {
            items(history) { shiftRecord ->
                HistoryRecordCard(
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                    tripRecord = shiftRecord.trip,
                    status = shiftRecord.status,
                    onRetrySend = {
                        // Show confirmation instead of sending immediately.
                        viewModel.requestRetrySend(shiftRecord.trip.uuid)
                    },
                    onNavigate = {
                        // No-op for now.
                    }
                )
            }
        }

        HistoryToNowDivider()

        if (selectedTrip != null) {
            CurrentTripCard(
                tripRecord = selectedTrip!!,
                onClick = { viewModel.finishCurrentTrip() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 16.dp)
            )
        } else {
            NewTripButton(
                onClick = { navController.navigate(Routes.TRIP_BY_NUMBER) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 16.dp)
            )
        }

        FinishTripResultBottomSheet(
            tripViewModel = viewModel,
            onDismissWithLogout = { }
        )

        // Retry confirmation dialog.
        RetrySendConfirmBottomSheet(tripViewModel = viewModel)

        // Retry result dialog.
        RetrySendResultBottomSheet(tripViewModel = viewModel)
    }
}
