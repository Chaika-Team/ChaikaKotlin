package com.example.chaika.ui.components.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.chaika.ui.dto.TripRecord
import com.example.chaika.ui.theme.TripDimens
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun TimeDetails(
    modifier: Modifier = Modifier,
    tripRecord: TripRecord
) {
    ConstraintLayout(
        modifier = modifier
            .height(TripDimens.TimeDetailsHeight)
            .width(TripDimens.TimeDetailsWidth)
    ) {
        val (startTime, time, arrow, endTime) = createRefs()

        Column(
            modifier = Modifier.constrainAs(startTime) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.percent(0.18f)
                height = Dimension.value(TripDimens.TimeDetailsHeight)
            },
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "${tripRecord.startTime.hour}:${tripRecord.startTime.minute.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
            )
        }

        Box(
            modifier = Modifier.constrainAs(time) {
                start.linkTo(startTime.end)
                end.linkTo(endTime.start)
                top.linkTo(parent.top)
                bottom.linkTo(arrow.top)
                width = Dimension.percent(0.64f)
                height = Dimension.percent(0.7f)
            },
            contentAlignment = Alignment.TopCenter
        ) {
            val duration = Duration.between(
                tripRecord.startTime.coerceAtMost(tripRecord.endTime),
                tripRecord.startTime.coerceAtLeast(tripRecord.endTime)
            )
            val totalMinutes = duration.toMinutes()
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            Text(
                text = "$hours ч $minutes мин",
                fontSize = 10.sp
            )
        }
        Box(
            modifier = Modifier.constrainAs(arrow) {
                start.linkTo(startTime.end)
                end.linkTo(endTime.start)
                top.linkTo(time.bottom)
                bottom.linkTo(parent.bottom)
                width = Dimension.percent(0.64f)
                height = Dimension.percent(0.3f)
            },
            contentAlignment = Alignment.TopCenter
        ) {
            Arrow()
        }

        Column(
            modifier = Modifier.constrainAs(endTime) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.percent(0.18f)
                height = Dimension.value(TripDimens.TimeDetailsHeight)
            },
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${tripRecord.endTime.hour}:${tripRecord.endTime.minute.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp
            )
        }
    }
}

@Preview
@Composable
fun TimeDetailsPreview() {
    TimeDetails(
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
        )
    )
}

