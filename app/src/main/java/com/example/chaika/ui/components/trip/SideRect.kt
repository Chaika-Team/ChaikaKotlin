package com.example.chaika.ui.components.trip

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.theme.TripDimens

@Composable
fun SideRect(
    modifier: Modifier
) {
    var colorScheme = MaterialTheme.colorScheme

    Box (
        modifier
            .fillMaxHeight()
            .width(TripDimens.SideRectWidth)
            .background(
                color = colorScheme.secondary
            )
    ) {
        Canvas(
            modifier = modifier
                .height(TripDimens.RecordCardHeight)
                .width(TripDimens.CardWidth)
        ) {
            val rightBorder = size.width
            drawLine(
                color = Color.Gray,
                start = Offset(rightBorder, 0f),
                end = Offset(rightBorder, size.height),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
            )
        }
    }

    // Пунктирная линия справа

}