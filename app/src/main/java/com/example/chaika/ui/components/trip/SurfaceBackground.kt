package com.example.chaika.ui.components.trip

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.theme.TripDimens

@Composable
fun SurfaceBackground(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    height: Dp = TripDimens.SearchCardHeight,
    backgroundColor: Color = Color.White,
    elevation: Dp = 10.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(elevation = elevation)
    ) {
        val width = size.width
        val heightPx = size.height
        val cornerRadiusPx = cornerRadius.toPx()

        val path = Path().apply {
            moveTo(0f, 0f)

            lineTo(0f, heightPx - cornerRadiusPx)

            quadraticTo(0f, heightPx, cornerRadiusPx, heightPx)

            lineTo(width - cornerRadiusPx, heightPx)

            quadraticTo(width, heightPx, width, heightPx - cornerRadiusPx)

            lineTo(width, 0f)

            close()
        }

        drawPath(
            path = path,
            color = backgroundColor
        )
    }
}