package com.example.chaika.ui.components.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.chaika.R
import com.example.chaika.ui.theme.TripDimens

@Composable
fun FinishCurrentTripButton(
    text: String = stringResource(R.string.finish_trip),
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = TripDimens.FinishTripWidth,
    height: Dp = TripDimens.FinishTripHeight,
    cornerRadius: Dp = TripDimens.SearchBarCornerRadius,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .testTag("finishTripButton")
            .width(width)
            .height(height),
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
        ),
        enabled = enabled
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = textStyle,
                maxLines = 1
            )
        }
    }
}