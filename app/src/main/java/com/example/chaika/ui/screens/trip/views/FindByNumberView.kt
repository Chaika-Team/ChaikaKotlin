package com.example.chaika.ui.screens.trip.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.chaika.ui.components.trip.SearchTripSurface
import com.example.chaika.ui.viewModels.TripViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import com.example.chaika.ui.navigation.Routes
import kotlinx.coroutines.delay
import com.example.chaika.R

@Composable
fun FindByNumberView(
    viewModel: TripViewModel,
    navController: NavController
) {
    val today = stringResource(R.string.today)
    var searchDate by rememberSaveable { mutableStateOf( today ) }
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
        SearchTripSurface(
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
            when (pagingData.loadState.refresh) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                        }
                    }

                is LoadState.Error -> {
                        // TODO()
                    }

                is LoadState.NotLoading -> {
                    if (pagingData.itemCount == 0) {
                        // TODO()
                    }
                }
            }
            
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
                            viewModel.createNewTrip()
                            viewModel.setSelectCarriage(tripRecord)
                            navController.navigate(Routes.TRIP_SELECT_CARRIAGE)
                        }
                    )
                }
            }
        }
    }
}