package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.R
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
            .testTag("historyRecordCard_${tripRecord.uuid}")
            .height(TripDimens.RecordCardHeight)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                onNavigate()
            }
    ) {
        HistoryRecordContent(
            modifier = Modifier
                .matchParentSize()
                .dashedBorder(color = borderColor),
            tripRecord = tripRecord,
            isError = isError
        )

        if (isError) {
            IconButton(
                onClick = onRetrySend,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(32.dp)
                    .testTag("historyRetrySend_${tripRecord.uuid}")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.retry_action),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
