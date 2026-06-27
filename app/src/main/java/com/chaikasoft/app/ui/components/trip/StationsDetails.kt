package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.ui.theme.TripDimens

@Composable
internal fun StationsDetails(tripRecord: TripDomain, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    val stationTextStyle = MaterialTheme.typography.bodySmall.toCompactStationTextStyle()

    Row(
        modifier = modifier.heightIn(min = TripDimens.StationsDetailsHeight),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = tripRecord.from.name,
                modifier = Modifier.fillMaxWidth(),
                style = stationTextStyle,
                color = colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = tripRecord.from.city,
                modifier = Modifier.fillMaxWidth(),
                style = stationTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = tripRecord.to.name,
                modifier = Modifier.fillMaxWidth(),
                style = stationTextStyle,
                color = colorScheme.secondary,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = tripRecord.to.city,
                modifier = Modifier.fillMaxWidth(),
                style = stationTextStyle,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun TextStyle.toCompactStationTextStyle(): TextStyle = copy(
    fontSize = TripDimens.StationTextFontSize,
    lineHeight = TripDimens.StationTextLineHeight,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(
        trim = LineHeightStyle.Trim.Both,
        alignment = LineHeightStyle.Alignment.Proportional
    )
)
