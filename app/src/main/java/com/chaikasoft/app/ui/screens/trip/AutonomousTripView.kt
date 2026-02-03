package com.chaikasoft.app.ui.screens.trip

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaikasoft.app.ui.components.template.ButtonSurface
import com.chaikasoft.app.ui.components.trip.DropDownMenu
import com.chaikasoft.app.ui.viewModels.AutonomousViewModel
import kotlinx.coroutines.flow.collectLatest
import android.app.DatePickerDialog
import android.text.format.DateFormat
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaikasoft.app.ui.navigation.Routes
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AutonomousTripScreen(
    viewModel: AutonomousViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { ev ->
            when (ev) {
                is AutonomousViewModel.Event.ShiftStarted -> {
                    navController.navigate(Routes.TRIP_MAIN) {
                        popUpTo(Routes.TRIP_GRAPH) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
                is AutonomousViewModel.Event.Info -> snackbarHostState.showSnackbar(ev.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ButtonSurface(
                    buttonText = if (state.isSubmitting) "СОХРАНЯЕМ..." else "ЗАВЕРШИТЬ",
                    onClick = {
                        viewModel.submit()
                        viewModel.clearState()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(8.dp))

            SectionTitle("Номер поезда")
            FocusHighlightField(
                value = state.trainNumber,
                onValueChange = viewModel::onTrainNumberChange,
                placeholder = "119A",
                imeAction = ImeAction.Next
            )

            Spacer(Modifier.height(8.dp))
            SectionHeader("Станции")

            Label("Станция отправления")
            DropDownMenu(
                modifier = Modifier.padding(horizontal = 16.dp),
                query = state.fromQuery,
                onQueryChange = viewModel::onFromQueryChange,
                suggestionsFlow = viewModel.fromSuggestions,
                onItemSelected = viewModel::onSelectFrom,
                placeholderText = "Укажите станцию отправления"
            )

            Spacer(Modifier.height(8.dp))
            Label("Станция прибытия")
            DropDownMenu(
                modifier = Modifier.padding(horizontal = 16.dp),
                query = state.toQuery,
                onQueryChange = viewModel::onToQueryChange,
                suggestionsFlow = viewModel.toSuggestions,
                onItemSelected = viewModel::onSelectTo,
                placeholderText = "Укажите станцию прибытия"
            )

            Spacer(Modifier.height(8.dp))
            SectionHeader("Время")

            TimeRow(
                arrival = state.arrival,
                onArrival = viewModel::onArrivalChange,
                departure = state.departure,
                onDeparture = viewModel::onDepartureChange
            )

            Spacer(Modifier.height(8.dp))
            SectionHeader("Вагон")

            Label("Номер вагона")
            FocusHighlightField(
                value = state.carriageNumber,
                onValueChange = viewModel::onCarriageNumberChange,
                placeholder = ""
            )

            Spacer(Modifier.height(8.dp))
            Label("Класс обслуживания")
            FocusHighlightField(
                value = state.carriageClassType,
                onValueChange = viewModel::onCarriageClassTypeChange,
                placeholder = "Не обязательно"
            )
        }
    }
}

/* ----------------------- Вспомогательные UI-компоненты ----------------------- */

@Composable
private fun FocusHighlightField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    imeAction: ImeAction = ImeAction.Done,
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onFocusChangedState: (Boolean) -> Unit = {},
) {
    val cs = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
        singleLine = true,
        isError = isError,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .heightIn(min = 48.dp)
            .onFocusChanged { onFocusChangedState(it.isFocused) },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onSearch = { onDone() },
            onDone = { onDone() }
        ),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = cs.primary,
            unfocusedBorderColor = cs.surfaceVariant,
            errorBorderColor = cs.error,
            focusedContainerColor = cs.primaryContainer,
            unfocusedContainerColor = cs.surfaceVariant,
            errorContainerColor = cs.errorContainer,
            cursorColor = cs.primary
        )
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = text,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SectionHeader(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun Label(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = text,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun TimeRow(
    arrival: LocalDateTime?,
    onArrival: (LocalDateTime?) -> Unit,
    departure: LocalDateTime?,
    onDeparture: (LocalDateTime?) -> Unit,
) {
    val context = LocalContext.current

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Pill(
            text = departure.formatOr("Сегодня, 22:00"),
            modifier = Modifier.weight(1f),
            onClick = {
                showDateTimePicker(
                    context = context,
                    initial = departure ?: LocalDateTime.now()
                ) { picked -> onDeparture(picked) }
            }
        )
        Pill(
            text = arrival.formatOr("Завтра, 21:32"),
            modifier = Modifier.weight(1f),
            onClick = {
                showDateTimePicker(
                    context = context,
                    initial = arrival  ?: LocalDateTime.now()
                ) { picked -> onArrival(picked) }
            }
        )
    }
}

@Composable
private fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            Text(
                text,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/** Открывает нативные DatePickerDialog -> TimePickerDialog и возвращает LocalDateTime */
private fun showDateTimePicker(
    context: Context,
    initial: LocalDateTime = LocalDateTime.now(),
    onPicked: (LocalDateTime) -> Unit
) {
    val initDate = initial.toLocalDate()
    val initTime = initial.toLocalTime()

    val dateDialog = DatePickerDialog(
        context,
        { _, year, month0, day ->
            val date = LocalDate.of(year, month0 + 1, day)

            val is24 = DateFormat.is24HourFormat(context)
            val timeDialog = TimePickerDialog(
                context,
                { _, hour, minute ->
                    val time = LocalTime.of(hour, minute)
                    onPicked(LocalDateTime.of(date, time))
                },
                initTime.hour,
                initTime.minute,
                is24
            )
            timeDialog.show()
        },
        initDate.year,
        initDate.monthValue - 1, // DatePickerDialog использует 0-индекс месяца
        initDate.dayOfMonth
    )

    dateDialog.show()
}

// форматтер для подписи
private fun LocalDateTime?.formatOr(fallback: String): String {
    if (this == null) return fallback
    return this.format(DateTimeFormatter.ofPattern("d MMM, HH:mm"))
}