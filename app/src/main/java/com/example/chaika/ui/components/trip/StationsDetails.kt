package com.example.chaika.ui.components.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.chaika.ui.dto.TripRecord
import com.example.chaika.ui.theme.TripDimens


@Composable
fun StationsDetails(
    modifier: Modifier = Modifier,
    tripRecord: TripRecord
) {
    var colorScheme = MaterialTheme.colorScheme
    ConstraintLayout(
        modifier = modifier
            .height(30.dp)
            .width(TripDimens.TimeDetailsWidth)
    ) {
        val (startDest, endDest) = createRefs()

        Column(
            modifier = Modifier.constrainAs(startDest) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.percent(0.5f)
                height = Dimension.fillToConstraints
            },
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = tripRecord.startName1,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.secondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = tripRecord.startName2,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier.constrainAs(endDest) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.percent(0.5f)
                height = Dimension.fillToConstraints
            },
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = tripRecord.endName1,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.secondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = tripRecord.endName2,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}