package com.chaikasoft.app.ui.components.statistics

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.chaikasoft.app.ui.theme.StatisticsDimens

@Composable
fun HeaderIconCell(@DrawableRes icon: Int, width: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.width(width),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(StatisticsDimens.HeaderIconSize)
        )
    }
}
