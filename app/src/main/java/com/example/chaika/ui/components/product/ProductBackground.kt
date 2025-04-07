package com.example.chaika.ui.components.product

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import com.example.chaika.ui.theme.ProductDimens
import kotlin.math.PI
import kotlin.math.tan

@Composable
fun ProductBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    cornerRadius: Dp = ProductDimens.ProductCornerRadius,
    elevation: Dp = ProductDimens.PaddingM,
    slantAngle: Float = ProductDimens.BackgroundSlantAngle
) {
    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = RoundedCornerShape(cornerRadius))
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val cornerPx = cornerRadius.toPx()
            val slantOffset = height * tan(slantAngle * (PI / 180f)).toFloat()

            val path = Path().apply {
                moveTo(slantOffset + cornerPx, 0f)
                quadraticTo(slantOffset, 0f, slantOffset, cornerPx)

                lineTo(0f, height - cornerPx)
                quadraticTo(0f, height, cornerPx, height)

                lineTo(width - cornerPx, height)
                quadraticTo(width, height, width, height - cornerPx)

                lineTo(width - slantOffset, cornerPx)
                quadraticTo(width - slantOffset, 0f, width - slantOffset - cornerPx, 0f)

                close()
            }

            drawPath(
                path = path,
                color = backgroundColor,
                style = Fill
            )
        }
    }
}