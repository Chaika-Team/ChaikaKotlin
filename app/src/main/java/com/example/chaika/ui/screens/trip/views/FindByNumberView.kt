package com.example.chaika.ui.screens.trip.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.chaika.ui.components.trip.FoundTripCard
import com.example.chaika.ui.components.trip.SearchCard
import com.example.chaika.ui.viewModels.TripViewModel
import androidx.compose.runtime.*
import com.example.chaika.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun FindByNumberView(
    viewModel: TripViewModel,
    navController: NavController
) {
    var searchDate by rememberSaveable { mutableStateOf("Сегодня") }
    var searchStart by rememberSaveable { mutableStateOf("") }
    var searchFinish by rememberSaveable { mutableStateOf("") }

    val pagingData = viewModel.pagingFoundTripsFlow.collectAsLazyPagingItems()


    LaunchedEffect(searchDate, searchStart, searchFinish) {
        delay(500)
        viewModel.getTrips(searchDate, searchStart, searchFinish)
    }

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        SearchCard(
            onSearch = { date, start, finish ->
                searchDate = date
                searchStart = start
                searchFinish = finish
            },
            initialDateValue = searchDate,
            initialStartValue = searchStart,
            initialFinishValue = searchFinish
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
                key = pagingData.itemKey{it.trainId}
            ) { index ->
                val tripRecord = pagingData[index]
                if (tripRecord != null) {
                    FoundTripCard(
                        modifier = Modifier,
                        tripRecord = tripRecord,
                        onClick = {
                            viewModel.setSelectCarriage(tripRecord)
                            navController.navigate(Routes.TRIP_SELECT_CARRIAGE)
                        }
                    )
                }
            }
        }
    }
}