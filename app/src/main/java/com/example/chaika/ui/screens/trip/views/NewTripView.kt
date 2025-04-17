package com.example.chaika.ui.screens.trip.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.chaika.ui.components.trip.HistoryRecordCard
import com.example.chaika.ui.components.trip.HistoryToNowDivider
import com.example.chaika.ui.components.trip.NewTripButton
import com.example.chaika.ui.dto.Route
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.viewModels.TripViewModel

@Composable
fun NewTripView(
    viewModel: TripViewModel,
    navController: NavController
) {
    val pagingData = viewModel.pagingDataFlow.collectAsLazyPagingItems()

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.weight(1f).padding(
                start = 24.dp,
                end = 24.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
            //contentPadding = PaddingValues(32.dp)
        ) {
            items(
                count = pagingData.itemCount,
                key = pagingData.itemKey{it.carriageID}
            ) { index ->
                val tripRecord = pagingData[index]
                if (tripRecord != null) {
                    HistoryRecordCard(
                        modifier = Modifier.padding(
                            top = 4.dp,
                            bottom = 4.dp
                        ),
                        tripRecord = tripRecord,
                        route = Route(
                            routeID = 0,
                            startName1 = "Московский вокзал",
                            startName2 = "Санкт-Петербург-Главный",
                            endName1 = "ТПУ черкизово",
                            endName2 = "Москва ВК Восточный"
                        )
                    )
                }
            }
        }

        HistoryToNowDivider(
            modifier = Modifier
        )

        NewTripButton(
            onClick = { navController.navigate(Routes.TRIP_BY_NUMBER) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 6.dp,
                    bottom = 16.dp
                )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }
}