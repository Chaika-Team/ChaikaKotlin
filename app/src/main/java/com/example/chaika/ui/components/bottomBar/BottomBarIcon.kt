package com.example.chaika.ui.components.bottomBar

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.theme.BarDimens
import com.example.chaika.ui.theme.ChaikaTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BottomBarIcon(
    imageVector: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Базовые размеры (эталонные)
    val baseScreenWidth = BarDimens.BarWidth
    val baseIconSize = BarDimens.IconSize

    // Получаем текущую ширину экрана
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Вычисляем коэффициент масштабирования
    val scaleFactor = screenWidth / baseScreenWidth

    // Применяем масштабирование
    val clickableSize = baseIconSize * scaleFactor
    val buttonSize = clickableSize * 0.33f

    ChaikaTheme {
        val colors = MaterialTheme.colorScheme
        val iconColor = if (selected) colors.primary else colors.secondary

        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(buttonSize)
                .clip(RoundedCornerShape(BarDimens.CornerShape * scaleFactor)),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = iconColor
            )
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = if (selected) colors.primary else colors.secondary,
                modifier = Modifier
                    .size(buttonSize)
            )
        }
    }
}