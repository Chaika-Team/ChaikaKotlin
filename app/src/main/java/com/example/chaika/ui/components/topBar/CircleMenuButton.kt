package com.example.chaika.ui.components.topBar

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chaika.R
import com.example.chaika.ui.components.trip.dashedBorder

@Composable
fun CircleMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    buttonSize: Dp = 40.dp,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.run {
            size(buttonSize).dashedBorder(cornerRadius = 24.dp)
        }
    ) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.menu),
            modifier = Modifier.size(iconSize),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}