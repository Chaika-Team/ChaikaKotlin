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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.chaika.R
import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.domain.models.trip.TripDomain
import com.example.chaika.ui.theme.TripDimens

@Composable
fun HistoryRecordContent(
    modifier: Modifier = Modifier,
    tripRecord: TripDomain,
) {
    val colorScheme = MaterialTheme.colorScheme

    ConstraintLayout(
        modifier = modifier
            .height(TripDimens.RecordCardHeight)
            .width(TripDimens.CardWidth)
    ) {
        val (sideRect, trainId, timeDetails, stationsDetails) = createRefs()

        SideRect(
            modifier = Modifier
                .constrainAs(sideRect) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.value(TripDimens.SideRectWidth)
                },
            color = colorScheme.secondary
        )

        Row(
            modifier = Modifier
                .constrainAs(trainId) {
                    start.linkTo(sideRect.end, margin = 4.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(timeDetails.top)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
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

        TimeDateDetails(
            tripRecord = tripRecord,
            modifier = Modifier
                .constrainAs(timeDetails) {
                    start.linkTo(sideRect.end, margin = 4.dp)
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
                    start.linkTo(sideRect.end, margin = 4.dp)
                    top.linkTo(timeDetails.bottom)
                    end.linkTo(parent.end, margin = 4.dp)
                    width = Dimension.fillToConstraints
                }
        )
    }
}

fun Modifier.dashedBorder(color: Color = Color.Gray, cornerRadius: Dp = 8.dp) = this.then(
    Modifier.drawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
            )
        )
    }
)

@Preview
@Composable
fun HistoryContent() {
    HistoryRecordContent(
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