package com.example.chaika.ui.screens.trip.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.chaika.ui.components.trip.FoundTripCard
import com.example.chaika.ui.components.trip.SearchCard
import com.example.chaika.ui.dto.Route
import com.example.chaika.ui.viewModels.TripViewModel

@Composable
fun FindByNumberView(
    viewModel: TripViewModel,
    navController: NavController
) {
    val pagingData = viewModel.pagingDataFlow.collectAsLazyPagingItems()

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        SearchCard(
            modifier = Modifier
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.weight(1f).padding(
                start = 24.dp,
                end = 24.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
        ) {
            items(
                count = pagingData.itemCount,
                key = pagingData.itemKey{it.carriageID}
            ) { index ->
                val tripRecord = pagingData[index]
                if (tripRecord != null) {
                    FoundTripCard(
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
    }

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }
}