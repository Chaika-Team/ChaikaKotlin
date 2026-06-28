package com.chaikasoft.app.ui.components.trip

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.theme.TripDimens
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
internal fun SearchTripSurface(
    modifier: Modifier = Modifier,
    height: Dp = TripDimens.SearchCardHeight,
    onSearch: (date: String, startStation: String, finishStation: String) -> Unit = { _, _, _ -> },
    initialDateValue: String = "Сегодня",
    initialStartValue: String = "",
    initialFinishValue: String = ""
) {
    var searchDate by rememberSaveable { mutableStateOf(initialDateValue) }
    var searchStart by rememberSaveable { mutableStateOf(initialStartValue) }
    var searchFinish by rememberSaveable { mutableStateOf(initialFinishValue) }

    LaunchedEffect(searchDate, searchStart, searchFinish) {
        snapshotFlow { Triple(searchDate, searchStart, searchFinish) }
            .debounce(400)
            .collect { (d, s, f) -> onSearch(d, s, f) }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = height)
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
                value = searchDate,
                onQueryChange = { newText ->
                    searchDate = newText
                    Log.d("SearchTripSurface", "Date changed: $newText")
                },
                placeholderText = stringResource(R.string.trip_search_date_placeholder),
                cornerRadius = TripDimens.SearchBarCornerRadius
            )

            SearchTripBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                value = searchStart,
                onQueryChange = { newText ->
                    searchStart = newText
                    Log.d("SearchTripSurface", "Start changed: $newText")
                },
                placeholderText = stringResource(R.string.trip_search_from_placeholder),
                cornerRadius = TripDimens.SearchBarCornerRadius
            )

            SearchTripBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                value = searchFinish,
                onQueryChange = { newText ->
                    searchFinish = newText
                    Log.d("SearchTripSurface", "Finish changed: $newText")
                },
                placeholderText = stringResource(R.string.trip_search_to_placeholder),
                cornerRadius = TripDimens.SearchBarCornerRadius
            )
        }
    }
}

@Preview
@Composable
private fun SearchCardPreview() {
    SearchTripSurface(modifier = Modifier)
}
