package com.example.chaika.ui.components.trip

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
import com.example.chaika.R
import com.example.chaika.ui.dto.TripRecord
import com.example.chaika.ui.theme.TripDimens
import java.time.LocalDateTime

@Composable
fun FoundTripContent(
    modifier: Modifier = Modifier,
    tripRecord: TripRecord
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
                text = tripRecord.trainId,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        TimeDetails(
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

        StationsDetails(
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
fun FoundTripContentPreview() {
    FoundTripContent(
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
    )
}