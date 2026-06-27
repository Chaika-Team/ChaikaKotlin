package com.chaikasoft.app.ui.screens.trip

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.ui.components.trip.FoundTripCard
import com.chaikasoft.app.ui.components.trip.SearchTripSurfaceDropdown
import com.chaikasoft.app.ui.state.TripsSearchUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun TripsSearchFilters(
    context: Context,
    searchDateIso: String,
    searchDateDisplay: String,
    fromQuery: String,
    toQuery: String,
    searchStartName: String?,
    searchFinishName: String?,
    fromSuggestions: LazyPagingItems<StationDomain>,
    toSuggestions: LazyPagingItems<StationDomain>,
    onSearchDateChanged: (String) -> Unit,
    onFromQueryChanged: (String) -> Unit,
    onToQueryChanged: (String) -> Unit,
    onStartStationChanged: (StationDomain?) -> Unit,
    onFinishStationChanged: (StationDomain?) -> Unit
) {
    SearchTripSurfaceDropdown(
        searchDateDisplay = searchDateDisplay,
        onSearchDateClick = {
            showDatePicker(
                context = context,
                currentDate = searchDateIso,
                onDatePicked = onSearchDateChanged
            )
        },
        fromQuery = fromQuery,
        onFromQueryChange = { query ->
            onFromQueryChanged(query)
            if (searchStartName != null && query != searchStartName) {
                onStartStationChanged(null)
            }
        },
        toQuery = toQuery,
        onToQueryChange = { query ->
            onToQueryChanged(query)
            if (searchFinishName != null && query != searchFinishName) {
                onFinishStationChanged(null)
            }
        },
        fromSuggestions = fromSuggestions,
        toSuggestions = toSuggestions,
        onStartStationChange = onStartStationChanged,
        onFinishStationChange = onFinishStationChanged
    )
}

@Composable
internal fun ColumnScope.TripsSearchContent(
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
                    .defaultMinSize(minHeight = 46.dp)
                    .widthIn(min = 200.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(fontSize = 18.sp, text = stringResource(R.string.retry_button))
            }
        }
    }
}

@Composable
private fun TripsGrid(trips: List<TripDomain>, onTripClick: (TripDomain) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tripsGrid")
    ) {
        items(trips, key = { it.uuid }) { trip ->
            FoundTripCard(
                tripRecord = trip,
                onClick = { onTripClick(trip) },
                modifier = Modifier
            )
        }
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
