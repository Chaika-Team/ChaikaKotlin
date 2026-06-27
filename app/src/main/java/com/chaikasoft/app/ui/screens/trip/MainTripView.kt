package com.chaikasoft.app.ui.screens.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.ui.components.trip.CurrentTripCard
import com.chaikasoft.app.ui.components.trip.DeleteTripConfirmBottomSheet
import com.chaikasoft.app.ui.components.trip.FinishTripConfirmBottomSheet
import com.chaikasoft.app.ui.components.trip.FinishTripResultBottomSheet
import com.chaikasoft.app.ui.components.trip.HistoryRecordCard
import com.chaikasoft.app.ui.components.trip.HistoryToNowDivider
import com.chaikasoft.app.ui.components.trip.NewTripButton
import com.chaikasoft.app.ui.components.trip.RetrySendConfirmBottomSheet
import com.chaikasoft.app.ui.components.trip.RetrySendResultBottomSheet
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.viewmodels.TripViewModel

@Composable
fun MainTripView(
    viewModel: TripViewModel,
    navController: NavController,
    openFinishTripConfirm: Boolean = false,
    onFinishTripConfirmConsumed: () -> Unit = {}
) {
    val history by viewModel.shiftHistory.collectAsStateWithLifecycle()
    val activeTrip by viewModel.activeTripRecord.collectAsStateWithLifecycle()
    val isFinishingTrip by viewModel.isFinishingTrip.collectAsStateWithLifecycle()
    val finishTripDialog by viewModel.finishTripDialog.collectAsStateWithLifecycle()
    val deleteTripDialog by viewModel.deleteTripDialog.collectAsStateWithLifecycle()
    val retryConfirm by viewModel.retryConfirm.collectAsStateWithLifecycle()
    val retryResult by viewModel.retryResult.collectAsStateWithLifecycle()
    var showFinishTripConfirmSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopHistoryObserving() }
    }

    LaunchedEffect(openFinishTripConfirm, activeTrip) {
        if (openFinishTripConfirm && activeTrip != null) {
            showFinishTripConfirmSheet = true
            onFinishTripConfirmConsumed()
        }
    }

    MainTripContent(
        history = history,
        activeTrip = activeTrip,
        isFinishingTrip = isFinishingTrip,
        onRetrySend = viewModel::requestRetrySend,
        onHistoryNavigate = { navController.navigate(Routes.historyStatistics(it)) },
        onCurrentTripClick = { showFinishTripConfirmSheet = true },
        onDeleteCurrentTripClick = viewModel::requestDeleteCurrentTrip,
        onNewTripClick = { navController.navigate(Routes.TRIP_BY_NUMBER) },
        modifier = Modifier
            .fillMaxSize()
            .testTag("tripMainScreen")
    )

    FinishTripConfirmBottomSheet(
        visible = showFinishTripConfirmSheet,
        onConfirm = {
            showFinishTripConfirmSheet = false
            viewModel.finishCurrentTrip()
        },
        onDismiss = { showFinishTripConfirmSheet = false }
    )

    FinishTripResultBottomSheet(
        messageRes = finishTripDialog?.messageRes,
        onDismiss = viewModel::dismissFinishTripDialog
    )

    deleteTripDialog?.let { dialog ->
        DeleteTripConfirmBottomSheet(
            hasPackageItems = dialog.hasPackageItems,
            preservePackage = dialog.preservePackage,
            isDeleting = dialog.isDeleting,
            errorMessageRes = dialog.errorMessageRes,
            onPreservePackageChanged = viewModel::onPreservePackageChanged,
            onConfirm = viewModel::confirmDeleteCurrentTrip,
            onDismiss = viewModel::dismissDeleteTripDialog
        )
    }

    RetrySendConfirmBottomSheet(
        visible = retryConfirm != null,
        onConfirm = viewModel::confirmRetrySend,
        onDismiss = viewModel::dismissRetryConfirm
    )

    RetrySendResultBottomSheet(
        messageRes = retryResult?.messageRes,
        onDismiss = viewModel::dismissRetryResult
    )
}

@Composable
private fun MainTripContent(
    history: List<ConductorTripShiftDomain>,
    activeTrip: TripDomain?,
    isFinishingTrip: Boolean,
    onRetrySend: (String) -> Unit,
    onHistoryNavigate: (String) -> Unit,
    onCurrentTripClick: () -> Unit,
    onDeleteCurrentTripClick: () -> Unit,
    onNewTripClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier
                .weight(1f)
                .testTag("tripHistoryList")
                .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 6.dp)
        ) {
            items(history, key = { it.trip.uuid }) { shiftRecord ->
                HistoryRecordCard(
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                    tripRecord = shiftRecord.trip,
                    status = shiftRecord.status,
                    onRetrySend = { onRetrySend(shiftRecord.trip.uuid) },
                    onNavigate = { onHistoryNavigate(shiftRecord.trip.uuid) }
                )
            }
        }

        HistoryToNowDivider()

        if (activeTrip != null) {
            CurrentTripCard(
                tripRecord = activeTrip,
                isFinishing = isFinishingTrip,
                onClick = onCurrentTripClick,
                onDeleteClick = onDeleteCurrentTripClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 16.dp)
            )
        } else {
            NewTripButton(
                onClick = onNewTripClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 16.dp)
            )
        }
    }
}

@PhoneScalablePreviews
@Composable
private fun MainTripContentPreview() {
    ChaikaTheme {
        MainTripContent(
            history = previewTripHistory(),
            activeTrip = previewTrip("active"),
            isFinishingTrip = false,
            onRetrySend = {},
            onHistoryNavigate = {},
            onCurrentTripClick = {},
            onDeleteCurrentTripClick = {},
            onNewTripClick = {}
        )
    }
}

@PhoneScalablePreviews
@Composable
private fun MainTripContentEmptyPreview() {
    ChaikaTheme {
        MainTripContent(
            history = emptyList(),
            activeTrip = null,
            isFinishingTrip = false,
            onRetrySend = {},
            onHistoryNavigate = {},
            onCurrentTripClick = {},
            onDeleteCurrentTripClick = {},
            onNewTripClick = {}
        )
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun MainTripContentWidePreview() {
    ChaikaTheme {
        MainTripContent(
            history = previewTripHistory(),
            activeTrip = previewTrip("active"),
            isFinishingTrip = false,
            onRetrySend = {},
            onHistoryNavigate = {},
            onCurrentTripClick = {},
            onDeleteCurrentTripClick = {},
            onNewTripClick = {}
        )
    }
}

private fun previewTripHistory(): List<ConductorTripShiftDomain> = listOf(
    ConductorTripShiftDomain(
        trip = previewTrip("sent"),
        activeCarriage = null,
        status = TripShiftStatusDomain.SENT
    ),
    ConductorTripShiftDomain(
        trip = previewTrip("error"),
        activeCarriage = null,
        status = TripShiftStatusDomain.FINISHED
    )
)

private fun previewTrip(uuid: String): TripDomain = TripDomain(
    uuid = uuid,
    trainNumber = "120A",
    departure = "2026-01-01T10:00:00+03:00",
    arrival = "2026-01-01T18:45:00+03:00",
    duration = "PT8H45M",
    from = StationDomain(
        code = "1",
        name = "Санкт-Петербург-Главный-Московский",
        city = "Санкт-Петербург"
    ),
    to = StationDomain(
        code = "2",
        name = "Москва Восточный вокзал Черкизово",
        city = "Москва"
    )
)
