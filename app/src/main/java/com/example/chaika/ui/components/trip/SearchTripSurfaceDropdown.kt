package com.example.chaika.ui.components.trip

import android.util.Log
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.theme.TripDimens
import com.example.chaika.domain.models.trip.StationDomain
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun SearchTripSurfaceDropdown(
    searchDate: String,
    onSearchDateChange: (String) -> Unit,
    onStartStationChange: (StationDomain?) -> Unit,
    onFinishStationChange: (StationDomain?) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = TripDimens.SearchCardHeight,
    suggestStations: suspend (String, Int) -> List<StationDomain>
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        SurfaceBackground(
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SearchTripBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                onQueryChange = onSearchDateChange,
                placeholderText = "Дата отправления",
                cornerRadius = TripDimens.SearchBarCornerRadius,
                initialQuery = searchDate
            )

            DropDownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                onQueryChange = { newText ->
                    Log.d("SearchTripSurface", "Start changed: $newText")
                },
                onItemSelected = { station ->
                    onStartStationChange(station)
                },
                placeholderText = "Станция отправки",
                cornerRadius = TripDimens.SearchBarCornerRadius,
                isStationSearch = true,
                initialQuery = "",
                suggestStations = suggestStations
            )

            DropDownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                onQueryChange = { newText ->
                    Log.d("SearchTripSurface", "Finish changed: $newText")
                },
                onItemSelected = { station ->
                    onFinishStationChange(station)
                },
                placeholderText = "Станция прибытия",
                cornerRadius = TripDimens.SearchBarCornerRadius,
                isStationSearch = true,
                initialQuery = "",
                suggestStations = suggestStations
            )
        }
    }
}