package com.example.chaika.ui.screens.trip.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.chaika.R
import com.example.chaika.ui.components.trip.HistoryRecordCard
import com.example.chaika.ui.components.trip.HistoryToNowDivider
import com.example.chaika.ui.components.trip.NewTripButton
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.theme.TripDimens
import com.example.chaika.ui.viewModels.TripViewModel

@Composable
fun NewTripView(
    viewModel: TripViewModel,
    navController: NavController
) {
    val pagingData = viewModel.pagingHistoryFlow.collectAsLazyPagingItems()

    val loadState = pagingData.loadState

    when {
        loadState.refresh is LoadState.Loading -> {
            CircularProgressIndicator()
        }
        loadState.refresh is LoadState.Error -> {
            val error = loadState.refresh as LoadState.Error
            Text(
                text = stringResource(R.string.error, error.error.localizedMessage ?: ""),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
        pagingData.itemCount == 0 -> {
            Text(
                text = stringResource(R.string.no_trip_history),
                modifier = Modifier.padding(16.dp)
            )
        }
    }


    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.weight(1f).padding(
                start = TripDimens.PaddingXL,
                end = TripDimens.PaddingXL,
                top = TripDimens.PaddingM,
                bottom = TripDimens.PaddingM
            ),
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
                        tripRecord = tripRecord
                    )
                }
            }
        }

        HistoryToNowDivider(
            modifier = Modifier
        )

        NewTripButton(
            onClick = {
                viewModel.setFindByNumber()
                navController.navigate(Routes.TRIP_BY_NUMBER)
                      },
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