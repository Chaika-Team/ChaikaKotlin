package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.TripDimens

@Composable
fun CurrentTripContent(
    modifier: Modifier = Modifier,
    tripRecord: TripDomain,
    heightTotal: Dp = TripDimens.CurrentTripCardHeight,
    widthTotal: Dp = TripDimens.CardWidth,
    isFinishing: Boolean = false,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .height(heightTotal)
            .width(widthTotal)
    ) {
        SideRect(
            modifier = Modifier.fillMaxHeight(),
            color = colorScheme.primary
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(
                    start = TripDimens.CurrentTripContentPadding,
                    end = TripDimens.CurrentTripContentPadding,
                    bottom = TripDimens.CurrentTripContentPadding
                )
        ) {
            Column(modifier = Modifier.fillMaxHeight()) {
                CurrentTripHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            end = TripDimens.IconSize + TripDimens.CurrentTripHeaderSpacing
                        ),
                    trainNumber = tripRecord.trainNumber
                )
                TimeDetails(
                    modifier = Modifier.fillMaxWidth(),
                    tripRecord = tripRecord
                )
                StationsDetails(
                    modifier = Modifier.fillMaxWidth(),
                    tripRecord = tripRecord
                )
                FinishCurrentTripButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onClick,
                    enabled = !isFinishing
                )
            }
            TripHeaderAction(
                modifier = Modifier.align(Alignment.TopEnd),
                testTag = "currentTripDelete_${tripRecord.uuid}",
                enabled = !isFinishing,
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.trip_delete_action),
                    tint = if (isFinishing) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = DISABLED_ICON_ALPHA)
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}

@Composable
private fun CurrentTripHeader(modifier: Modifier = Modifier, trainNumber: String) {
    Row(
        modifier = modifier.height(TripDimens.CurrentTripHeaderHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TripDimens.CurrentTripHeaderSpacing)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_train),
                contentDescription = stringResource(R.string.train_ic),
                modifier = Modifier.size(TripDimens.IconSize)
            )
            Text(
                text = trainNumber,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TripHeaderAction(
    modifier: Modifier = Modifier,
    testTag: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(TripDimens.HeaderActionTouchTarget)
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.TopEnd
    ) {
        Box(modifier = Modifier.size(TripDimens.IconSize)) {
            content()
        }
    }
}

private const val DISABLED_ICON_ALPHA = 0.38f

@Preview
@Composable
fun CurrentTripButtonPreview() {
    ChaikaTheme {
        CurrentTripContent(
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
            onClick = { },
            onDeleteClick = { }
        )
    }
}
