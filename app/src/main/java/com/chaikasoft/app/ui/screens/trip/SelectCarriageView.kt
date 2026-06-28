package com.chaikasoft.app.ui.screens.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.components.trip.SelectedTripRecordSurface
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.theme.ChaikaTheme
import com.chaikasoft.app.ui.theme.PhoneScalablePreviews
import com.chaikasoft.app.ui.theme.PhoneWideNoBreakPreview
import com.chaikasoft.app.ui.theme.TripDimens
import com.chaikasoft.app.ui.viewmodels.TripViewModel

@Composable
fun SelectCarriageView(viewModel: TripViewModel, navController: NavController) {
    val selectedTrip by viewModel.selectedTripForCreation.collectAsStateWithLifecycle()
    val carriageNumber by viewModel.carriageNumber.collectAsStateWithLifecycle()
    val isCarriageInputValid by viewModel.isCarriageInputValid.collectAsStateWithLifecycle()
    val startShiftErrorMessageRes by
        viewModel.startShiftErrorMessageRes.collectAsStateWithLifecycle()

    SelectCarriageContent(
        selectedTrip = selectedTrip,
        carriageNumber = carriageNumber,
        isCarriageInputValid = isCarriageInputValid,
        startShiftErrorMessageRes = startShiftErrorMessageRes,
        onCarriageNumberChanged = viewModel::onCarriageNumberChanged,
        onConfirm = {
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
        }
    )
}

@Composable
private fun SelectCarriageContent(
    selectedTrip: TripDomain?,
    carriageNumber: String,
    isCarriageInputValid: Boolean,
    startShiftErrorMessageRes: Int?,
    onCarriageNumberChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
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
                        tripRecord = selectedTrip
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
                        onValueChange = onCarriageNumberChanged,
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

                    startShiftErrorMessageRes?.let { messageRes ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(messageRes),
                            color = colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                ButtonSurface(
                    buttonText = stringResource(R.string.confirm),
                    onClick = onConfirm,
                    enabled = isCarriageInputValid,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@PhoneScalablePreviews
@Composable
private fun SelectCarriageContentPreview() {
    ChaikaTheme {
        SelectCarriageContent(
            selectedTrip = previewSelectedTrip(),
            carriageNumber = "12",
            isCarriageInputValid = true,
            startShiftErrorMessageRes = null,
            onCarriageNumberChanged = {},
            onConfirm = {}
        )
    }
}

@PhoneWideNoBreakPreview
@Composable
private fun SelectCarriageContentWidePreview() {
    ChaikaTheme {
        SelectCarriageContent(
            selectedTrip = previewSelectedTrip(),
            carriageNumber = "12",
            isCarriageInputValid = true,
            startShiftErrorMessageRes = null,
            onCarriageNumberChanged = {},
            onConfirm = {}
        )
    }
}

private fun previewSelectedTrip(): TripDomain = TripDomain(
    uuid = "preview-trip",
    trainNumber = "120A",
    departure = "2026-01-01T10:00:00+03:00",
    arrival = "2026-01-01T18:45:00+03:00",
    duration = "PT8H45M",
    from = StationDomain("2004000", "Санкт-Петербург-Главный-Московский", "Санкт-Петербург"),
    to = StationDomain("2000001", "Москва Восточный вокзал", "Москва")
)
