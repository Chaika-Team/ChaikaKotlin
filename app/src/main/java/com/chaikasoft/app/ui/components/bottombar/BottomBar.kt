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
import com.chaikasoft.app.ui.navigation.NavigationGuards
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.BarDimens

@Composable
fun BottomBar(
    navController: NavController,
    currentRoute: String?,
    hasActiveShift: Boolean,
    onBlockedNavigation: () -> Unit
) {
    if (currentRoute == null || currentRoute in Routes.routesWithoutBottomBar) return

    fun navigateToMainGraph(route: String, popUpToRoute: String = route) {
        if (NavigationGuards.isProtectedBottomGraph(route) && !hasActiveShift) {
            onBlockedNavigation()
            return
        }

        navController.navigate(route) {
            popUpTo(popUpToRoute) { inclusive = route == popUpToRoute }
            launchSingleTop = true
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
        modifier = Modifier
            .fillMaxWidth()
            .height(BarDimens.BarHeight),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarIcon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_train),
            selected = Routes.mainRoutes[Routes.TRIP]?.contains(currentRoute) == true,
            tag = "bottomBarTrip",
            onClick = {
                navigateToMainGraph(
                    route = Routes.TRIP_MAIN,
                    popUpToRoute = Routes.TRIP_GRAPH
                )
            }
        )
        BottomBarIcon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_bag),
            selected = Routes.mainRoutes[Routes.PRODUCT]?.contains(currentRoute) == true,
            tag = "bottomBarProduct",
            onClick = {
                navigateToMainGraph(Routes.PRODUCT_GRAPH)
            }
        )
        BottomBarIcon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_statistics),
            selected = Routes.mainRoutes[Routes.STATISTICS]?.contains(currentRoute) == true,
            tag = "bottomBarStatistics",
            onClick = {
                navigateToMainGraph(Routes.STATISTICS_GRAPH)
            }
        )
        BottomBarIcon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_time),
            selected = Routes.mainRoutes[Routes.OPERATION]?.contains(currentRoute) == true,
            tag = "bottomBarOperation",
            onClick = {
                navigateToMainGraph(Routes.OPERATION_GRAPH)
            }
        )
        BottomBarIcon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_profile_bar),
            selected = Routes.mainRoutes[Routes.PROFILE]?.contains(currentRoute) == true,
            tag = "bottomBarProfile",
            onClick = {
                navController.navigate(Routes.PROFILE) {
                    popUpTo(Routes.PROFILE_GRAPH) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}
