package com.example.chaika.ui.components.bottomBar


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomBarBox(
    @DrawableRes iconRes: Int, // ID ресурса иконки
    selected: Boolean,
    onClick: () -> Unit
) {
    // Базовые размеры (эталонные)
    val baseScreenWidth = 375.dp
    val baseIconSize = 80.dp

    // Получаем текущую ширину экрана
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Вычисляем коэффициент масштабирования
    val scaleFactor = screenWidth / baseScreenWidth

    // Применяем масштабирование
    val clickableSize = baseIconSize * scaleFactor
    val iconSize = clickableSize * 0.5f // Делаем область клика немного больше иконки

    val iconColor = if (selected) Color(0xFFE21A1A) else Color(0xFF9E9E9E)
    Box(
        modifier = Modifier
            .size(clickableSize)
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(8.dp * scaleFactor)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            colorFilter = ColorFilter.tint(iconColor)
        )
    }
}