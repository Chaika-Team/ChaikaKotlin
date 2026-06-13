package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.ui.theme.TripDimens

@Composable
fun SelectedTripRecordSurface(
    tripRecord: TripDomain,
    modifier: Modifier = Modifier,
    height: Dp = TripDimens.FoundTripCardHeight + TripDimens.PaddingXL * 2
) {
    Box(
        modifier.fillMaxWidth().height(height),
        contentAlignment = Alignment.Center
    ) {
        SurfaceBackground(
            height = height
        )
        FoundTripContent(
            tripRecord = tripRecord,
            modifier = Modifier.matchParentSize().padding(24.dp)
        )
    }
}
