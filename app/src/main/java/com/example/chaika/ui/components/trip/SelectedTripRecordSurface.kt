package com.example.chaika.ui.components.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chaika.domain.models.trip.TripDomain

@Composable
fun SelectedTripRecordSurface(
    modifier: Modifier = Modifier,
    height: Dp,
    tripRecord: TripDomain
) {
    Box(
        modifier.fillMaxWidth().height(height),
        contentAlignment = Alignment.Center
    ) {
        SurfaceBackground(
            height = height
        )
        FoundTripContent(
            modifier = Modifier.matchParentSize().padding(24.dp),
            tripRecord = tripRecord,
        )
    }
}