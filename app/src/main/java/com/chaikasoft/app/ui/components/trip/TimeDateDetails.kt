package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.ui.theme.TripDimens
import com.chaikasoft.app.util.parseTripDetails

@Composable
internal fun TimeDateDetails(tripRecord: TripDomain, modifier: Modifier = Modifier) {
    val tripDetails = tripRecord.parseTripDetails()

    ConstraintLayout(
        modifier = modifier
            .height(TripDimens.TimeDateDetailsHeight)
    ) {
        val (startTime, time, arrow, endTime) = createRefs()

        Column(
            modifier = Modifier.constrainAs(startTime) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.value(TripDimens.TimeDateDetailsHeight)
            },
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            DayMonthText(tripDetails.departureDayMonth)
            TimeValueText(tripDetails.departureTime)
        }

        Box(
            modifier = Modifier
                .constrainAs(time) {
                    start.linkTo(startTime.end)
                    end.linkTo(endTime.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(arrow.top, margin = TripTime.DurationArrowGap)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
            contentAlignment = Alignment.TopCenter
        ) {
            DurationText(tripDetails.durationHours, tripDetails.durationMinutes)
        }

        Box(
            modifier = Modifier
                .constrainAs(arrow) {
                    start.linkTo(startTime.end)
                    end.linkTo(endTime.start)
                    top.linkTo(time.bottom)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                },
            contentAlignment = Alignment.TopCenter
        ) {
            ArrowPadded(modifier = Modifier) // паддинги берутся из общих дефолтов
        }

        Column(
            modifier = Modifier.constrainAs(endTime) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.value(TripDimens.TimeDateDetailsHeight)
            },
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            DayMonthText(tripDetails.arrivalDayMonth)
            TimeValueText(tripDetails.arrivalTime)
        }
    }
}

@Preview
@Composable
private fun TimeDateDetailsPreview() {
    TimeDateDetails(
        tripRecord = TripDomain(
            uuid = "12",
            trainNumber = "120A",
            departure = "2025-03-29T23:55:00+03:00",
            arrival = "2025-03-30T09:47:00+03:00",
            duration = "PT9H52M",
            from = StationDomain(
                code = "1",
                name = "Московский вокзал",
                city = "Санкт-Петербург-Главный"
            ),
            to = StationDomain(
                code = "2",
                name = "ТПУ Черкизово",
                city = "Москва ВК Восточный"
            )
        ),
        modifier = Modifier
    )
}
