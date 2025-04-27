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
import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.domain.models.trip.TripDomain
import com.example.chaika.ui.theme.TripDimens
import com.example.chaika.util.parseTripDetails

@Composable
fun TimeDetails(
    modifier: Modifier = Modifier,
    tripRecord: TripDomain
) {
    val tripDetails = tripRecord.parseTripDetails()

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
                text = tripDetails.departureTime,
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
            Text(text = "${tripDetails.durationHours} ч ${tripDetails.durationMinutes} мин")
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
                text = tripDetails.arrivalTime,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
            )
        }
    }
}

@Preview
@Composable
fun TimeDetailsPreview() {
    TimeDetails(
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
        )
    )
}

