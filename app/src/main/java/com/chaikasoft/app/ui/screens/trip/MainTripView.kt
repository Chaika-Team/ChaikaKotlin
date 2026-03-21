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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaikasoft.app.ui.components.trip.currentTripCard
import com.chaikasoft.app.ui.components.trip.finishTripResultBottomSheet
import com.chaikasoft.app.ui.components.trip.historyRecordCard
import com.chaikasoft.app.ui.components.trip.historyToNowDivider
import com.chaikasoft.app.ui.components.trip.newTripButton
import com.chaikasoft.app.ui.components.trip.retrySendConfirmBottomSheet
import com.chaikasoft.app.ui.components.trip.retrySendResultBottomSheet
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.viewModels.TripViewModel

@Composable
fun mainTripView(
    viewModel: TripViewModel,
    navController: NavController,
) {
    val history by viewModel.shiftHistory.collectAsStateWithLifecycle()
    val selectedTrip by viewModel.selectedTripRecord.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
        viewModel.checkActiveShift()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopHistoryObserving() }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            reverseLayout = true,
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 6.dp)
        ) {
            items(history) { shiftRecord ->
                historyRecordCard(
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

        historyToNowDivider()

        if (selectedTrip != null) {
            currentTripCard(
                tripRecord = selectedTrip!!,
                onClick = { viewModel.finishCurrentTrip() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 16.dp)
            )
        } else {
            newTripButton(
                onClick = { navController.navigate(Routes.TRIP_BY_NUMBER) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 16.dp)
            )
        }

        finishTripResultBottomSheet(
            tripViewModel = viewModel,
            onDismissWithLogout = { }
        )

        // Retry confirmation dialog.
        retrySendConfirmBottomSheet(tripViewModel = viewModel)

        // Retry result dialog.
        retrySendResultBottomSheet(tripViewModel = viewModel)
    }
}
