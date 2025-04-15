package com.example.chaika.ui.components.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.chaika.ui.dto.Route
import com.example.chaika.ui.dto.TripRecord
import com.example.chaika.ui.theme.TripDimens
import java.time.LocalDateTime

@Composable
fun HistoryRecordCard(
    modifier: Modifier = Modifier,
    tripRecord: TripRecord,
    route: Route
) {
    Box(
        modifier = modifier
            .height(TripDimens.RecordCardHeight)
            .width(TripDimens.CardWidth)
            .background(
                color = Color.White,
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
    ) {
        HistoryRecordContent(
            modifier = Modifier.matchParentSize(),
            tripRecord = tripRecord,
            route = route,
        )
    }
}

@Preview
@Composable
fun HistoryPreview() {
    HistoryRecordCard(
        modifier = Modifier,
        tripRecord = TripRecord(
            routeID = 0,
            trainId = "119A",
            startTime = LocalDateTime.parse("2024-03-30T00:12:00"),
            endTime = LocalDateTime.parse("2024-03-30T09:47:00"),
            carriageID = 33
        ),
        route = Route(
            routeID = 0,
            startName1 = "Московский вокзал",
            startName2 = "Санкт-Петербург-Главный",
            endName1 = "ТПУ черкизово",
            endName2 = "Москва ВК Восточный"
        )
    )
}