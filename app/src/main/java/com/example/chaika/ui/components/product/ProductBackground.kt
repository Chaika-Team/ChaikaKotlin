package com.example.chaika.ui.components.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.tan

@Composable
fun ProductBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color.Black,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 30.dp,
    slantAngle: Float = 1f // Угол наклона в градусах
) {
    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = RoundedCornerShape(cornerRadius))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height
            val cornerPx = cornerRadius.toPx()
            val slantOffset = height * tan(slantAngle * (PI / 180f)).toFloat()
            val borderPx = borderWidth.toPx()

            val path = Path().apply {
                // Левый верхний угол (с скруглением)
                moveTo(slantOffset + cornerPx, 0f)
                quadraticBezierTo(slantOffset, 0f, slantOffset, cornerPx)

                // Левый нижний угол
                lineTo(0f, height - cornerPx)
                quadraticBezierTo(0f, height, cornerPx, height)

                // Правый нижний угол
                lineTo(width - cornerPx, height)
                quadraticBezierTo(width, height, width, height - cornerPx)

                // Правый верхний угол (с наклоном)
                lineTo(width - slantOffset, cornerPx)
                quadraticBezierTo(width - slantOffset, 0f, width - slantOffset - cornerPx, 0f)

                close()
            }

            // Заливка
            drawPath(
                path = path,
                color = backgroundColor,
                style = Fill
            )

            // Обводка
//            drawPath(
//                path = path,
//                color = borderColor,
//                style = Stroke(width = borderPx)
//            )
        }
    }
}