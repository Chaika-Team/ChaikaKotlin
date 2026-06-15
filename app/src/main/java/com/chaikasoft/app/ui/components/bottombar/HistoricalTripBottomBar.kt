package com.chaikasoft.app.ui.components.bottombar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.theme.BarDimens

enum class HistoricalTripBottomBarTab {
    STATISTICS,
    OPERATIONS
}

@Composable
fun HistoricalTripBottomBar(
    selectedTab: HistoricalTripBottomBarTab?,
    onTabClick: (HistoricalTripBottomBarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomBarBackground(
        modifier = Modifier.shadow(
            elevation = 10.dp,
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp
            ),
            clip = true
        ),
        height = BarDimens.BarHeight
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(BarDimens.BarHeight),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarIcon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_statistics),
            selected = selectedTab == HistoricalTripBottomBarTab.STATISTICS,
            tag = "historicalBottomBarStatistics",
            onClick = { onTabClick(HistoricalTripBottomBarTab.STATISTICS) }
        )
        BottomBarIcon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_time),
            selected = selectedTab == HistoricalTripBottomBarTab.OPERATIONS,
            tag = "historicalBottomBarOperation",
            onClick = { onTabClick(HistoricalTripBottomBarTab.OPERATIONS) }
        )
    }
}
