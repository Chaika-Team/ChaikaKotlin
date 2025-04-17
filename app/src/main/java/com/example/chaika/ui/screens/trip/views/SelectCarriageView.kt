package com.example.chaika.ui.screens.trip.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chaika.R
import com.example.chaika.ui.components.trip.CarriageCard
import com.example.chaika.ui.components.trip.SelectedTripRecordSurface
import com.example.chaika.ui.theme.TripDimens
import com.example.chaika.ui.viewModels.TripViewModel

@Composable
fun SelectCarriageView(
    viewModel: TripViewModel,
    navController: NavController
) {
    val pagingItems = viewModel.pagingCarriageFlow.collectAsLazyPagingItems()

    val groupedCarriages = remember(pagingItems) {
        derivedStateOf {
            pagingItems.itemSnapshotList.items.groupBy { it.classType }
        }
    }

    when {
        viewModel.getSelectedTrip() == null -> {
            Text(
                text = stringResource(R.string.no_selected_trip_error),
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        viewModel.getSelectedTrip()?.let {
            SelectedTripRecordSurface(
                height = TripDimens.FoundTripCardHeight + TripDimens.PaddingXL + TripDimens.PaddingXL,
                tripRecord = it
            )
        }

        LazyColumn {
            groupedCarriages.value.forEach { (classType, carriages) ->
                item(key = "header_$classType") {
                    Text(
                        text = stringResource(R.string.carriage_class) + " $classType",
                        modifier = Modifier.padding(8.dp),
                    )
                }
                items(
                    count = carriages.size,
                    key = { index -> carriages[index].id }
                ) { index ->
                    val carriage = carriages[index]
                    CarriageCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        carriageId = carriage.id,
                        onClick = {  }
                    )
                }
            }
        }
    }
}