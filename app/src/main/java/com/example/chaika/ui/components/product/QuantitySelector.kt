package com.example.chaika.ui.components.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.chaika.ui.theme.ProductDimens.QuantitySelectorHeight

@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
    colorBack: Color = MaterialTheme.colorScheme.primary,
    colorText: Color = MaterialTheme.colorScheme.background,
    cornerRadius: Dp = QuantitySelectorHeight // Добавляем параметр для радиуса скругления
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(QuantitySelectorHeight)
            .background(
                color = colorBack,
                shape = RoundedCornerShape(cornerRadius) // Скругляем углы фона
            )
            .clip(RoundedCornerShape(cornerRadius)), // Обрезаем контент по тем же скруглениям
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = onDecrease,
            modifier = Modifier.size(QuantitySelectorHeight * 1.2F),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = colorText
            )
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = colorText
            )
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 18.sp,
            color = colorText
        )

        IconButton(
            onClick = onIncrease,
            modifier = Modifier.size(QuantitySelectorHeight * 1.2F),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = colorText
            )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Increase",
                tint = colorText
            )
        }
    }
}