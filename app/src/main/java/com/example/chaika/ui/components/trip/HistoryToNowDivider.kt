package com.example.chaika.ui.components.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chaika.R

@Composable
fun HistoryToNowDivider(
    modifier: Modifier
) {
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(
                start = 24.dp,
                end = 24.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
            text = stringResource(R.string.history),
            fontSize = 11.sp,
            color = Color.LightGray
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 24.dp, end = 24.dp),
            thickness = 1.dp, color = Color.LightGray
        )
        Text(
            modifier = Modifier.padding(
                start = 24.dp,
                end = 24.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
            text = stringResource(R.string.now),
            fontSize = 11.sp,
            color = Color.LightGray
        )
    }

}