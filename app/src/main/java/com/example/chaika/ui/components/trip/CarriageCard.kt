package com.example.chaika.ui.components.trip

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chaika.ui.theme.TripDimens

@Composable
fun CarriageCard(
    modifier: Modifier,
    carriageId: Int,
    onClick: () -> Unit,
    height: Dp = TripDimens.CarriageCardHeight,
    width: Dp = TripDimens.CarriageCardWidth
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(TripDimens.CornerRadius),
                clip = true
            )
            .background(Color.White, RoundedCornerShape(TripDimens.CornerRadius))
            .clickable(onClick = onClick)
            .height(height)
            .width(width),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = Color.Black,
                cornerRadius = CornerRadius(TripDimens.CornerRadius.toPx()),
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 0f)
                )
            )
        }

        Text(
            text = carriageId.toString(),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}