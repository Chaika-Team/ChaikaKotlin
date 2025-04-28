package com.example.chaika.ui.screens.trip.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chaika.R
import com.example.chaika.ui.components.trip.CarriageCard
import com.example.chaika.ui.components.trip.SelectedTripRecordSurface
import com.example.chaika.ui.components.util.EmptyState
import com.example.chaika.ui.navigation.Routes
import com.example.chaika.ui.theme.TripDimens
import com.example.chaika.ui.viewModels.TripViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.testTag

@Composable
fun SelectCarriageView(
    viewModel: TripViewModel,
    navController: NavController
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val carriages by viewModel.carriageList.collectAsState()
    val selectedTrip by remember { derivedStateOf { viewModel.getSelectedTrip() } }


    Column(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            selectedTrip == null -> {
                Text(
                    text = stringResource(R.string.no_selected_trip_error),
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                SelectedTripRecordSurface(
                    height = TripDimens.FoundTripCardHeight + TripDimens.PaddingXL * 2,
                    tripRecord = selectedTrip!!
                )

                val groupedCarriages = carriages.groupBy { it.classType }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("carriageList")            // ← вот этот тег
                ) {
                    if (groupedCarriages.isEmpty()) {
                        item {
                            EmptyState(
                                message = stringResource(R.string.empty_carriages_message),
                                modifier = Modifier.fillParentMaxSize()
                            )
                        }
                    } else {
                        groupedCarriages.forEach { (classType, subCarriages) ->
                            item(key = "header_$classType") {
                                Text(
                                    text = stringResource(R.string.carriage_class) + " $classType",
                                    modifier = Modifier.padding(8.dp),
                                )
                            }
                            items(subCarriages) { carriage ->
                                CarriageCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                        .testTag("carriageCard"),
                                    carriageId = carriage.carNumber.toInt(),
                                    onClick = {
                                        viewModel.setCurrentTrip(carriage = carriage)
                                        navController.navigate(Routes.TRIP_CURRENT)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}