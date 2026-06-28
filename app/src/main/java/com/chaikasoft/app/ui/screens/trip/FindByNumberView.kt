package com.chaikasoft.app.ui.screens.trip

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.state.TripsSearchUiState
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.viewmodels.TripViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf

@Composable
fun FindByNumberView(viewModel: TripViewModel, navController: NavController) {
    val searchDate by viewModel.searchDate.collectAsStateWithLifecycle()
    val fromQuery by viewModel.fromQuery.collectAsStateWithLifecycle()
    val toQuery by viewModel.toQuery.collectAsStateWithLifecycle()
    val searchStart by viewModel.searchStartStation.collectAsStateWithLifecycle()
    val searchFinish by viewModel.searchFinishStation.collectAsStateWithLifecycle()
    val searchDateDisplay = formatIsoDateForDisplay(searchDate)
    val fromSuggestions = viewModel.fromSuggestions.collectAsLazyPagingItems()
    val toSuggestions = viewModel.toSuggestions.collectAsLazyPagingItems()

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

    FindByNumberContent(
        searchDateIso = searchDate,
        searchDateDisplay = searchDateDisplay,
        fromQuery = fromQuery,
        toQuery = toQuery,
        searchStartName = searchStart?.name,
        searchFinishName = searchFinish?.name,
        fromSuggestions = fromSuggestions,
        toSuggestions = toSuggestions,
        tripsState = tripsState,
        onSearchDateChanged = viewModel::onSearchDateChanged,
        onFromQueryChanged = viewModel::onFromQueryChanged,
        onToQueryChanged = viewModel::onToQueryChanged,
        onStartStationChanged = viewModel::onStartStationChanged,
        onFinishStationChanged = viewModel::onFinishStationChanged,
        onRetry = viewModel::retryTrips,
        onTripClick = { trip ->
            viewModel.preserveSearchForBackNavigation()
            viewModel.selectTrip(trip)
            navigateToSelectCarriage(navController)
        }
    )
}

@Composable
private fun FindByNumberContent(
    searchDateIso: String,
    searchDateDisplay: String,
    fromQuery: String,
    toQuery: String,
    searchStartName: String?,
    searchFinishName: String?,
    fromSuggestions: LazyPagingItems<StationDomain>,
    toSuggestions: LazyPagingItems<StationDomain>,
    tripsState: TripsSearchUiState,
    onSearchDateChanged: (String) -> Unit,
    onFromQueryChanged: (String) -> Unit,
    onToQueryChanged: (String) -> Unit,
    onStartStationChanged: (StationDomain?) -> Unit,
    onFinishStationChanged: (StationDomain?) -> Unit,
    onRetry: () -> Unit,
    onTripClick: (TripDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("findTripScreen")
    ) {
        TripsSearchFilters(
            context = context,
            searchDateIso = searchDateIso,
            searchDateDisplay = searchDateDisplay,
            fromQuery = fromQuery,
            toQuery = toQuery,
            searchStartName = searchStartName,
            searchFinishName = searchFinishName,
            fromSuggestions = fromSuggestions,
            toSuggestions = toSuggestions,
            onSearchDateChanged = onSearchDateChanged,
            onFromQueryChanged = onFromQueryChanged,
            onToQueryChanged = onToQueryChanged,
            onStartStationChanged = onStartStationChanged,
            onFinishStationChanged = onFinishStationChanged
        )

        TripsSearchContent(
            tripsState = tripsState,
            onRetry = onRetry,
            onTripClick = onTripClick
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

private fun navigateToSelectCarriage(navController: NavController) {
    try {
        navController.navigate(Routes.TRIP_SELECT_CARRIAGE)
    } catch (e: IllegalStateException) {
        Log.e("Navigation", "Failed to navigate", e)
    }
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

@PhoneScalablePreviews
@Composable
private fun FindByNumberContentPreview() {
    val emptyStations = flowOf(PagingData.empty<StationDomain>()).collectAsLazyPagingItems()
    ChaikaTheme {
        FindByNumberContent(
            searchDateIso = "2026-01-01",
            searchDateDisplay = "1 янв. 2026 г.",
            fromQuery = "Санкт-Петербург-Главный-Московский",
            toQuery = "Москва Восточный вокзал",
            searchStartName = "Санкт-Петербург-Главный-Московский",
            searchFinishName = "Москва Восточный вокзал",
            fromSuggestions = emptyStations,
            toSuggestions = emptyStations,
            tripsState = TripsSearchUiState.Content(previewTrips()),
            onSearchDateChanged = {},
            onFromQueryChanged = {},
            onToQueryChanged = {},
            onStartStationChanged = {},
            onFinishStationChanged = {},
            onRetry = {},
            onTripClick = {}
        )
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun FindByNumberContentWidePreview() {
    val emptyStations = flowOf(PagingData.empty<StationDomain>()).collectAsLazyPagingItems()
    ChaikaTheme {
        FindByNumberContent(
            searchDateIso = "2026-01-01",
            searchDateDisplay = "1 янв. 2026 г.",
            fromQuery = "Санкт-Петербург-Главный-Московский",
            toQuery = "Москва Восточный вокзал",
            searchStartName = "Санкт-Петербург-Главный-Московский",
            searchFinishName = "Москва Восточный вокзал",
            fromSuggestions = emptyStations,
            toSuggestions = emptyStations,
            tripsState = TripsSearchUiState.Content(previewTrips()),
            onSearchDateChanged = {},
            onFromQueryChanged = {},
            onToQueryChanged = {},
            onStartStationChanged = {},
            onFinishStationChanged = {},
            onRetry = {},
            onTripClick = {}
        )
    }
}

private fun previewTrips(): List<TripDomain> = listOf(
    TripDomain(
        uuid = "preview-trip-1",
        trainNumber = "120A",
        departure = "2026-01-01T10:00:00+03:00",
        arrival = "2026-01-01T18:45:00+03:00",
        duration = "PT8H45M",
        from = StationDomain("2004000", "Санкт-Петербург-Главный-Московский", "Санкт-Петербург"),
        to = StationDomain("2000001", "Москва Восточный вокзал", "Москва")
    )
)
