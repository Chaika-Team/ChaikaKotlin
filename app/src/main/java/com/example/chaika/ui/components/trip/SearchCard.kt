package com.example.chaika.ui.components.trip

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

@Composable
fun SearchCard(
    height: Dp = TripDimens.SearchCardHeight,
    modifier: Modifier = Modifier
) {
    var searchDate by rememberSaveable { mutableStateOf("") }
    var searchStart by rememberSaveable { mutableStateOf("") }
    var searchFinish by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        SearchCardBackground(
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
                    println(newText)
                    searchDate = newText
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
                    println(newText)
                    searchStart = newText
                },
                placeholderText = "Станция отправки",
                cornerRadius = TripDimens.SearchBarCornerRadius
            )

            SearchTripBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                onQueryChange = { newText ->
                    println(newText)
                    searchFinish = newText
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
    SearchCard(modifier = Modifier)
}