package com.chaikasoft.app.ui.screens.trip.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chaikasoft.app.ui.components.trip.HistoryRecordCard
import com.chaikasoft.app.ui.components.trip.HistoryToNowDivider
import com.chaikasoft.app.ui.components.trip.NewTripButton
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.TripDimens
import com.chaikasoft.app.ui.viewModels.TripViewModel
import androidx.compose.foundation.lazy.grid.items


@Composable
fun NewTripView(
    viewModel: TripViewModel,
    navController: NavController
) {
    val pagingData by viewModel.pagingHistoryFlow.collectAsState()

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
            items(pagingData) { tripRecord ->

                    HistoryRecordCard(
                        modifier = Modifier.padding(
                            top = 4.dp,
                            bottom = 4.dp
                        ),
                        tripRecord = tripRecord
                    )

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