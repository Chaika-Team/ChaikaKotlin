package com.chaikasoft.app.ui.screens.trip

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.ui.components.trip.FoundTripCard
import com.chaikasoft.app.ui.components.trip.SearchTripSurfaceDropdown
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.state.TripsSearchUiState
import com.chaikasoft.app.ui.viewmodels.TripViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun FindByNumberView(viewModel: TripViewModel, navController: NavController) {
    val context = LocalContext.current
    val searchDate by viewModel.searchDate.collectAsStateWithLifecycle()
    val fromQuery by viewModel.fromQuery.collectAsStateWithLifecycle()
    val toQuery by viewModel.toQuery.collectAsStateWithLifecycle()
    val searchStart by viewModel.searchStartStation.collectAsStateWithLifecycle()
    val searchFinish by viewModel.searchFinishStation.collectAsStateWithLifecycle()
    val searchDateDisplay = formatIsoDateForDisplay(searchDate)

    val tripsState by viewModel.tripsSearchState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onFindByNumberScreenShown()
    }

    ObserveTripsSearchEffect(
        searchDate = searchDate,
        searchStartCode = searchStart?.code,
        searchFinishCode = searchFinish?.code,
        onSearch = viewModel::getTrips,
        onReset = viewModel::resetTripsSearchResults
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("findTripScreen")
    ) {
        TripsSearchFilters(
            context = context,
            viewModel = viewModel,
            searchDateIso = searchDate,
            searchDateDisplay = searchDateDisplay,
            fromQuery = fromQuery,
            toQuery = toQuery,
            searchStartName = searchStart?.name,
            searchFinishName = searchFinish?.name
        )

        TripsSearchContent(
            tripsState = tripsState,
            onRetry = viewModel::retryTrips,
            onTripClick = { trip ->
                viewModel.preserveSearchForBackNavigation()
                viewModel.selectTrip(trip)
                navigateToSelectCarriage(navController)
            }
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun ObserveTripsSearchEffect(
    searchDate: String,
    searchStartCode: String?,
    searchFinishCode: String?,
    onSearch: (String, String, String) -> Unit,
    onReset: () -> Unit
) {
    val latestParams by rememberUpdatedState(Triple(searchDate, searchStartCode, searchFinishCode))

    LaunchedEffect(Unit) {
        snapshotFlow { latestParams }
            .debounce(500)
            .distinctUntilChanged()
            .collectLatest { (date, from, to) ->
                if (from != null && to != null && date.isNotBlank()) {
                    onSearch(date, from, to)
                    Log.d("FindByNumberView", "Search params: date=$date from=$from to=$to")
                } else {
                    onReset()
                }
            }
    }
}

@Composable
private fun TripsSearchFilters(
    context: Context,
    viewModel: TripViewModel,
    searchDateIso: String,
    searchDateDisplay: String,
    fromQuery: String,
    toQuery: String,
    searchStartName: String?,
    searchFinishName: String?
) {
    SearchTripSurfaceDropdown(
        searchDateDisplay = searchDateDisplay,
        onSearchDateClick = {
            showDatePicker(
                context = context,
                currentDate = searchDateIso,
                onDatePicked = viewModel::onSearchDateChanged
            )
        },
        fromQuery = fromQuery,
        onFromQueryChange = { query ->
            viewModel.onFromQueryChanged(query)
            if (searchStartName != null && query != searchStartName) {
                viewModel.onStartStationChanged(null)
            }
        },
        toQuery = toQuery,
        onToQueryChange = { query ->
            viewModel.onToQueryChanged(query)
            if (searchFinishName != null && query != searchFinishName) {
                viewModel.onFinishStationChanged(null)
            }
        },
        fromSuggestions = viewModel.fromSuggestions,
        toSuggestions = viewModel.toSuggestions,
        onStartStationChange = viewModel::onStartStationChanged,
        onFinishStationChange = viewModel::onFinishStationChanged
    )
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.TripsSearchContent(
    tripsState: TripsSearchUiState,
    onRetry: () -> Unit,
    onTripClick: (TripDomain) -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .testTag("tripsSearchContent")
            .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        when (tripsState) {
            TripsSearchUiState.Idle -> Unit
            TripsSearchUiState.Loading -> TripsLoading()
            TripsSearchUiState.Empty -> TripsEmpty()
            is TripsSearchUiState.Error -> TripsError(tripsState, onRetry)
            is TripsSearchUiState.Content -> TripsGrid(tripsState.trips, onTripClick)
        }
    }
}

@Composable
private fun TripsLoading() {
    CircularProgressIndicator(modifier = Modifier.testTag("tripsLoading"))
}

@Composable
private fun TripsEmpty() {
    Text(
        text = stringResource(R.string.trip_search_empty),
        modifier = Modifier.testTag("tripsEmpty")
    )
}

@Composable
private fun TripsError(errorState: TripsSearchUiState.Error, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(errorState.messageRes))
        if (errorState.retryable) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .testTag("tripsRetryButton")
                    .padding(16.dp)
                    .height(46.dp)
                    .width(200.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(fontSize = 18.sp, text = stringResource(R.string.retry_button))
            }
        }
    }
}

@Composable
private fun TripsGrid(trips: List<TripDomain>, onTripClick: (TripDomain) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier
            .fillMaxSize()
            .testTag("tripsGrid")
    ) {
        items(trips) { trip ->
            FoundTripCard(
                modifier = Modifier,
                tripRecord = trip,
                onClick = { onTripClick(trip) }
            )
        }
    }
}

private fun navigateToSelectCarriage(navController: NavController) {
    try {
        navController.navigate(Routes.TRIP_SELECT_CARRIAGE)
    } catch (e: IllegalStateException) {
        Log.e("Navigation", "Failed to navigate", e)
    }
}

private fun showDatePicker(context: Context, currentDate: String, onDatePicked: (String) -> Unit) {
    val initialDate = runCatching {
        LocalDate.parse(currentDate, DateTimeFormatter.ISO_LOCAL_DATE)
    }.getOrElse { LocalDate.now() }

    DatePickerDialog(
        context,
        { _, year, month0, day ->
            val selectedDate = LocalDate.of(year, month0 + 1, day)
            onDatePicked(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
        },
        initialDate.year,
        initialDate.monthValue - 1,
        initialDate.dayOfMonth
    ).show()
}

private fun formatIsoDateForDisplay(isoDate: String): String {
    if (isoDate.isBlank()) return ""

    val formatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())

    return runCatching {
        LocalDate.parse(isoDate, DateTimeFormatter.ISO_LOCAL_DATE).format(formatter)
    }.getOrElse { isoDate }
}
