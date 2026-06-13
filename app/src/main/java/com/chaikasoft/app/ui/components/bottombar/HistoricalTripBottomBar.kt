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
import androidx.navigation.NavController
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.BarDimens

@Composable
fun HistoricalTripBottomBar(
    navController: NavController,
    currentRoute: String?,
    shiftUuid: String,
    modifier: Modifier = Modifier
) {
    fun navigateToHistoricalTab(route: String) {
        val alreadySelected =
            (
                Routes.isHistoricalStatisticsRoute(currentRoute) &&
                    Routes.isHistoricalStatisticsRoute(route)
                ) ||
                (
                    Routes.isHistoricalOperationsRoute(currentRoute) &&
                        Routes.isHistoricalOperationsRoute(route)
                    )
        if (alreadySelected) {
            return
        }

        navController.navigate(route) {
            popUpTo(Routes.HISTORY_GRAPH) {
                inclusive = false
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

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
            selected = Routes.isHistoricalStatisticsRoute(currentRoute),
            tag = "historicalBottomBarStatistics",
            onClick = { navigateToHistoricalTab(Routes.historyStatistics(shiftUuid)) }
        )
        BottomBarIcon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_time),
            selected = Routes.isHistoricalOperationsRoute(currentRoute),
            tag = "historicalBottomBarOperation",
            onClick = { navigateToHistoricalTab(Routes.historyOperations(shiftUuid)) }
        )
    }
}
