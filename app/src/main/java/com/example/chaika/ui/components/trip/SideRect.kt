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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.theme.TripDimens

@Composable
fun SideRect(
    modifier: Modifier,
    width: Dp = TripDimens.SideRectWidth,
    height: Dp = TripDimens.RecordCardHeight,
    color: Color
) {
    Box (
        modifier = modifier
            .fillMaxHeight()
            .width(width)
            .background(color = color)
    ) {
        Canvas(
            modifier = Modifier
                .height(height)
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
}

@Preview
@Composable
fun SideRectPreview() {
    val colorScheme = MaterialTheme.colorScheme
    SideRect(modifier = Modifier, color = colorScheme.primary)
}