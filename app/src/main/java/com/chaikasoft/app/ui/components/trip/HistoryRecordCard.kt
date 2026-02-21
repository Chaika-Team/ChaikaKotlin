package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.ui.theme.TripDimens

@Composable
fun HistoryRecordCard(
    modifier: Modifier = Modifier,
    tripRecord: TripDomain,
    status: TripShiftStatusDomain,
    onRetrySend: () -> Unit = {},
    onNavigate: () -> Unit = {}
) {
    val isError = status != TripShiftStatusDomain.SENT
    val borderColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = modifier
            .height(TripDimens.RecordCardHeight)
            .width(TripDimens.CardWidth)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                if (isError) onRetrySend() else onNavigate()
            }
    ) {
        HistoryRecordContent(
            modifier = Modifier
                .matchParentSize()
                .dashedBorder(color = borderColor),
            tripRecord = tripRecord,
            isError = isError
        )
    }
}