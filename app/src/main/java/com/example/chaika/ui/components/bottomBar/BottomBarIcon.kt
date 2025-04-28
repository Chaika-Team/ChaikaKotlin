package com.example.chaika.ui.components.bottomBar

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.chaika.ui.theme.BarDimens

@Composable
fun BottomBarIcon(
    imageVector: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier // ← добавили сюда
) {
    val colors = MaterialTheme.colorScheme
    val iconColor = if (selected) colors.primary else colors.secondary

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(BarDimens.IconSize)
            .clip(RoundedCornerShape(BarDimens.CornerShape)),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = iconColor
        )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Navigation icon",
            tint = if (selected) colors.primary else colors.secondary,
            modifier = Modifier.size(BarDimens.IconSize)
        )
    }
}
