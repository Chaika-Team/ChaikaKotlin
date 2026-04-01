package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.ui.theme.TripDimens
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@OptIn(FlowPreview::class)
@Composable
fun SearchTripSurfaceDropdown(
    searchDateDisplay: String,
    onSearchDateClick: () -> Unit,

    fromQuery: String,
    onFromQueryChange: (String) -> Unit,
    toQuery: String,
    onToQueryChange: (String) -> Unit,

    fromSuggestions: Flow<PagingData<StationDomain>>,
    toSuggestions: Flow<PagingData<StationDomain>>,

    onStartStationChange: (StationDomain?) -> Unit,
    onFinishStationChange: (StationDomain?) -> Unit,

    modifier: Modifier = Modifier,
    height: Dp = TripDimens.SearchCardHeight,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        SurfaceBackground(modifier = Modifier.matchParentSize())

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SearchTripBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                value = searchDateDisplay,
                onQueryChange = {},
                placeholderText = stringResource(R.string.trip_search_date_placeholder),
                cornerRadius = TripDimens.SearchBarCornerRadius,
                readOnly = true,
                onClick = onSearchDateClick
            )

            DropDownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                query = fromQuery,
                onQueryChange = onFromQueryChange,
                suggestionsFlow = fromSuggestions,
                onItemSelected = { station ->
                    onStartStationChange(station)
                },
                placeholderText = stringResource(R.string.trip_search_from_placeholder),
                cornerRadius = TripDimens.SearchBarCornerRadius
            )

            DropDownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                query = toQuery,
                onQueryChange = onToQueryChange,
                suggestionsFlow = toSuggestions,
                onItemSelected = { station ->
                    onFinishStationChange(station)
                },
                placeholderText = stringResource(R.string.trip_search_to_placeholder),
                cornerRadius = TripDimens.SearchBarCornerRadius
            )
        }
    }
}
