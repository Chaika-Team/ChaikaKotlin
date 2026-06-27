package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.ui.theme.TripDimens

@Composable
internal fun HistoryRecordContent(
    modifier: Modifier = Modifier,
    tripRecord: TripDomain,
    isError: Boolean = false,
    onRetrySend: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val sideColor = if (isError) colorScheme.error else colorScheme.secondary
    val actionEndPadding = TripDimens.IconSize + TripDimens.HistoryRecordHeaderSpacing

    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .heightIn(min = TripDimens.RecordCardHeight)
            .fillMaxWidth()
    ) {
        SideRect(
            modifier = Modifier.fillMaxHeight(),
            color = sideColor
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(
                    start = TripDimens.HistoryRecordContentPadding,
                    end = TripDimens.HistoryRecordContentPadding,
                    bottom = TripDimens.HistoryRecordContentPadding
                )
        ) {
            Column {
                HistoryRecordHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isError) {
                                Modifier.padding(end = actionEndPadding)
                            } else {
                                Modifier
                            }
                        ),
                    trainNumber = tripRecord.trainNumber,
                    isError = isError
                )
                TimeDateDetails(
                    modifier = Modifier.fillMaxWidth(),
                    tripRecord = tripRecord
                )
                StationsDetails(
                    modifier = Modifier.fillMaxWidth(),
                    tripRecord = tripRecord
                )
            }
            if (isError) {
                TripHeaderAction(
                    modifier = Modifier.align(Alignment.TopEnd),
                    testTag = "historyRetrySend_${tripRecord.uuid}",
                    onClick = onRetrySend
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.retry_action),
                        tint = colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryRecordHeader(
    modifier: Modifier = Modifier,
    trainNumber: String,
    isError: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier.heightIn(min = TripDimens.HistoryRecordHeaderHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TripDimens.HistoryRecordHeaderSpacing)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_train),
                contentDescription = stringResource(R.string.train_ic),
                modifier = Modifier.size(TripDimens.IconSize),
                tint = if (isError) colorScheme.error else LocalContentColor.current
            )
            Text(
                text = trainNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) colorScheme.error else Color.Unspecified
            )
        }
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
