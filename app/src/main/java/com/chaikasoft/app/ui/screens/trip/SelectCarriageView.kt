package com.chaikasoft.app.ui.screens.trip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chaikasoft.app.R
import com.chaikasoft.app.ui.components.trip.SelectedTripRecordSurface
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.TripDimens
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaikasoft.app.ui.viewModels.TripViewModel

@Composable
fun SelectCarriageView(
    viewModel: TripViewModel,
    navController: NavController
) {
    val selectedTrip by viewModel.selectedTripRecord.collectAsStateWithLifecycle()
    val carriageNumber by viewModel.carriageNumber.collectAsStateWithLifecycle()
    val isCarriageInputValid by viewModel.isCarriageInputValid.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        when {
            selectedTrip == null -> {
                Text(
                    text = stringResource(R.string.no_selected_trip_error),
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                Column(modifier = Modifier.weight(1f)) {
                    SelectedTripRecordSurface(
                        height = TripDimens.FoundTripCardHeight + TripDimens.PaddingXL * 2,
                        tripRecord = selectedTrip!!
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.enter_carriage_data),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = carriageNumber,
                        onValueChange = { viewModel.onCarriageNumberChanged(it) },
                        label = { Text(stringResource(R.string.carriage_number)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(TripDimens.CornerRadius),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surfaceVariant,
                            unfocusedContainerColor = colorScheme.surfaceVariant,
                            disabledContainerColor = colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }

                ButtonSurface(
                    buttonText = stringResource(R.string.confirm),
                    onClick = {
                        viewModel.confirmCarriageInput(
                            onSuccess = {
                                navController.navigate(Routes.TRIP_MAIN) {
                                    popUpTo(Routes.TRIP_GRAPH) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    },
                    enabled = isCarriageInputValid,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
