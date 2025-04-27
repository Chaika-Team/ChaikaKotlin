package com.example.chaika.ui.components.trip

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.example.chaika.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chaika.ui.theme.TripDimens


@Composable
fun NewTripButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .testTag("newTripButton")
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(TripDimens.CornerRadius),
                clip = true
            )
            .background(Color.White, RoundedCornerShape(TripDimens.CornerRadius))
            .clickable(onClick = onClick)
            .height(TripDimens.NewTripButtonHeight)
            .width(TripDimens.NewTripButtonWidth),
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

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_plus),
            contentDescription = stringResource(id = R.string.add_trip),
            modifier = Modifier.size(TripDimens.IconSize),
            tint = Color.Black
        )
    }
}

@Preview
@Composable
fun NewTripButtonPreview() {
    NewTripButton () {  }
}
