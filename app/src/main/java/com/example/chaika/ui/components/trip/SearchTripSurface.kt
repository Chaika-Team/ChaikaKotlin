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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.theme.TripDimens
import androidx.compose.runtime.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun SearchTripSurface(
    height: Dp = TripDimens.SearchCardHeight,
    modifier: Modifier = Modifier,
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
            .height(height),
    ) {
        SurfaceBackground(
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SearchTripBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                onQueryChange = { newText ->
                    searchDate = newText
                    Log.d("SearchTripSurface", "Date changed: $newText")
                                },
                placeholderText = "Дата отправления",
                cornerRadius = TripDimens.SearchBarCornerRadius,
                initialQuery = "Сегодня"
            )

            SearchTripBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                onQueryChange = { newText ->
                    searchStart = newText
                    Log.d("SearchTripSurface", "Start changed: $newText")
                },
                placeholderText = "Станция отправки",
                cornerRadius = TripDimens.SearchBarCornerRadius
            )

            SearchTripBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                onQueryChange = { newText ->
                    searchFinish = newText
                    Log.d("SearchTripSurface", "Finish changed: $newText")
                },
                placeholderText = "Станция прибытия",
                cornerRadius = TripDimens.SearchBarCornerRadius
            )
        }
    }
}

@Preview
@Composable
fun SearchCardPreview() {
    SearchTripSurface(modifier = Modifier)
}