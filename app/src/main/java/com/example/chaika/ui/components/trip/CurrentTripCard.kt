package com.example.chaika.ui.components.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.domain.models.trip.TripDomain
import com.example.chaika.ui.theme.TripDimens

@Composable
fun CurrentTripCard(
    modifier: Modifier = Modifier,
    tripRecord: TripDomain,
    heightTotal: Dp = TripDimens.NewTripButtonHeight,
    widthTotal: Dp = TripDimens.CardWidth,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(heightTotal)
            .width(widthTotal)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
    ) {
        CurrentTripContent(
            modifier = Modifier.matchParentSize().dashedBorder(),
            tripRecord = tripRecord,
            heightTotal = heightTotal,
            widthTotal = widthTotal,
            onClick = onClick
        )
    }
}

@Preview
@Composable
fun CurrentTripCardPreview() {
    CurrentTripCard (
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
        onClick = { }
    )
}