package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.ui.theme.TripDimens

@Composable
fun foundTripContent(
    modifier: Modifier = Modifier,
    tripRecord: TripDomain
) {
    ConstraintLayout(
        modifier = modifier
            .height(TripDimens.FoundTripCardHeight)
            .width(TripDimens.CardWidth)
    ) {
        val (trainId, timeDetails, stationsDetails) = createRefs()

        Row(
            modifier = Modifier
                .constrainAs(trainId) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(timeDetails.top)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_train),
                contentDescription = stringResource(R.string.train_ic),
                modifier = Modifier.size(TripDimens.IconSize)
            )
            Text(
                text = tripRecord.trainNumber,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        timeDetails(
            tripRecord = tripRecord,
            modifier = Modifier
                .constrainAs(timeDetails) {
                    start.linkTo(parent.start, margin = 4.dp)
                    top.linkTo(trainId.bottom)
                    end.linkTo(parent.end, margin = 4.dp)
                    bottom.linkTo(stationsDetails.top)
                    width = Dimension.fillToConstraints
                }
        )

        stationsDetails(
            tripRecord = tripRecord,
            modifier = Modifier
                .constrainAs(stationsDetails) {
                    start.linkTo(parent.start, margin = 4.dp)
                    top.linkTo(timeDetails.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )
    }
}

@Preview
@Composable
fun foundTripContentPreview() {
    foundTripContent(
        modifier = Modifier,
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
    )
}
