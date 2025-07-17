package com.example.chaika.ui.components.product

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chaika.R
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.foundation.shape.CircleShape
import com.example.chaika.ui.theme.ProductDimens
import androidx.compose.ui.res.stringResource

@Composable
fun ArrowFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color.White,
        contentColor = Color.Black,
        modifier = modifier.size(ProductDimens.ArrowFAB.Size),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp, focusedElevation = 0.dp, hoveredElevation = 0.dp),
        shape = CircleShape
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = stringResource(id = R.string.arrow_fab_content_description),
            tint = Color.Black
        )
    }
} 