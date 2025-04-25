package com.example.chaika.ui.components.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chaika.R
import com.example.chaika.ui.theme.ChaikaTheme
import com.example.chaika.ui.theme.TripDimens

@Composable
fun HistoryToNowDivider(
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(

            modifier = Modifier.padding(
                horizontal = TripDimens.PaddingXL,
                vertical = TripDimens.PaddingM
            ),
            text = stringResource(R.string.history),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = TripDimens.PaddingXL),
            thickness = 1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

        )
        Text(
            modifier = Modifier.padding(
                horizontal = TripDimens.PaddingXL,
                vertical = TripDimens.PaddingM
            ),
            text = stringResource(R.string.now),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Preview
@Composable
fun HistoryToNowDividerPreview() {
    ChaikaTheme {
        HistoryToNowDivider()
    }
}