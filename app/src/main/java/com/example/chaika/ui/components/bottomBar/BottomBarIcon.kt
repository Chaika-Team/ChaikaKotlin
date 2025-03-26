package com.example.chaika.ui.components.bottomBar

import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
fun BottomBarIcon(
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
    val buttonSize = clickableSize * 0.5f // Делаем область клика немного больше иконки

    val iconColor = if (selected) Color(0xFFE21A1A) else Color(0xFF9E9E9E)

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(buttonSize)
            .clip(RoundedCornerShape(8.dp * scaleFactor)),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = iconColor
        )
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(buttonSize),
            tint = iconColor
            //colorFilter = ColorFilter.tint(iconColor)
        )
    }
}