package com.example.chaika.ui.components.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.domain.models.trip.TripDomain
import com.example.chaika.ui.theme.TripDimens

@Composable
fun FoundTripCard(
    modifier: Modifier = Modifier,
    tripRecord: TripDomain,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .height(TripDimens.FoundTripCardHeight)
            .width(TripDimens.CardWidth)
            .background(
                color = colorScheme.background,
            )
            .clickable(onClick = onClick)
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter),
            thickness = 1.dp,
            color = colorScheme.onSurfaceVariant
        )
        FoundTripContent(
            modifier = Modifier.matchParentSize(),
            tripRecord = tripRecord,
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomCenter),
            thickness = 1.dp,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun FoundPreview() {
    FoundTripCard(
        modifier = Modifier,
        tripRecord = TripDomain(
            uuid = "12",
            trainNumber = "120A",
            departure = "2025-03-29T23:55:00+03:00",
            arrival = "2025-03-30T09:47:00+03:00",
            duration = "PT9H52M",
            from = StationDomain(
                code = 1,
                name = "Московский вокзал",
                city = "Санкт-Петербург-Главный"
            ),
            to = StationDomain(
                code = 2,
                name = "ТПУ Черкизово",
                city = "Москва ВК Восточный"
            )
        ),
        onClick = {}
    )
}