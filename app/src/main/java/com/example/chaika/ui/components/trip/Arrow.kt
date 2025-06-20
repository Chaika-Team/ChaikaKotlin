package com.example.chaika.ui.components.trip

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Arrow(
    modifier: Modifier = Modifier,
    pointSize: Dp = 8.dp,
    spaceAfterPoint: Dp = 2.dp,
    color: Color = Color.Black
) {
    val density = LocalDensity.current
    val pointSizePx = with(density) { pointSize.toPx() }
    val spacePx = with(density) { spaceAfterPoint.toPx() }
    val endPaddingPx = 4f

    Canvas(modifier = modifier.fillMaxWidth()) {
        val canvasWidth = size.width
        val lineStartX = pointSizePx + spacePx
        val lineEndX = canvasWidth - endPaddingPx

        drawCircle(
            color = color,
            radius = pointSizePx / 2,
            center = Offset(x = pointSizePx / 2, y = size.height / 2)
        )

        drawLine(
            color = color,
            start = Offset(x = lineStartX, y = size.height / 2),
            end = Offset(x = lineEndX, y = size.height / 2),
            strokeWidth = 2f
        )

        val arrowHeadSize = 16f
        val path = Path().apply {
            moveTo(lineEndX, size.height / 2)
            lineTo(lineEndX - arrowHeadSize, size.height / 2 - arrowHeadSize)
            moveTo(lineEndX, size.height / 2)
            lineTo(lineEndX - arrowHeadSize, size.height / 2 + arrowHeadSize)
        }

        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
    }
}

@Preview
@Composable
fun ArrowPoint() {
    Arrow()
}