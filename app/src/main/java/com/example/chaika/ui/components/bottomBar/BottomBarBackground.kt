package com.example.chaika.ui.components.bottomBar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BottomBarBackground(
    modifier: Modifier = Modifier,
    height: Dp = 80.dp,
    cornerRadius: Dp = 20.dp,
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

        // Создаем путь только с верхними скругленными углами
        val path = Path().apply {
            // Начинаем с левого нижнего угла (без скругления)
            moveTo(0f, heightPx)

            // Линия к началу левого скругления
            lineTo(0f, cornerRadiusPx)

            // Левый верхний скругленный угол
            quadraticTo(0f, 0f, cornerRadiusPx, 0f)

            // Линия к началу правого скругления
            lineTo(width - cornerRadiusPx, 0f)

            // Правый верхний скругленный угол
            quadraticTo(width, 0f, width, cornerRadiusPx)

            // Линия к правому нижнему углу (без скругления)
            lineTo(width, heightPx)

            // Замыкаем путь
            close()
        }

        // Рисуем только по заданному пути
        drawPath(
            path = path,
            color = backgroundColor
        )
    }
}

@Preview
@Composable
private fun Background(
) {
    BottomBarBackground()
}