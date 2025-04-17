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
import com.example.chaika.ui.dto.TripRecord
import com.example.chaika.ui.theme.TripDimens
import java.time.LocalDateTime

@Composable
fun FoundTripCard(
    modifier: Modifier = Modifier,
    tripRecord: TripRecord,
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
            color = colorScheme.onSurface
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
            color = colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun FoundPreview() {
    FoundTripCard(
        modifier = Modifier,
        tripRecord = TripRecord(
            routeID = 0,
            trainId = "119A",
            startTime = LocalDateTime.parse("2024-03-30T00:12:00"),
            endTime = LocalDateTime.parse("2024-03-30T09:47:00"),
            carriageID = 33,
            startName1 = "Московский вокзал",
            startName2 = "Санкт-Петербург-Главный",
            endName1 = "ТПУ черкизово",
            endName2 = "Москва ВК Восточный"
        ),
        onClick = {}
    )
}