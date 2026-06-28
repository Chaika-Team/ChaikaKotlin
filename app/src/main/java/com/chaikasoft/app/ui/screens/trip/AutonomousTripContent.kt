package com.chaikasoft.app.ui.screens.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.ui.components.trip.DropDownMenu
import com.chaikasoft.app.ui.viewmodels.AutonomousViewModel
import java.time.LocalDateTime

@Composable
internal fun AutonomousTripContent(
    state: AutonomousViewModel.UiState,
    fromSuggestions: LazyPagingItems<StationDomain>,
    toSuggestions: LazyPagingItems<StationDomain>,
    onTrainNumberChange: (String) -> Unit,
    onFromQueryChange: (String) -> Unit,
    onSelectFrom: (StationDomain) -> Unit,
    onToQueryChange: (String) -> Unit,
    onSelectTo: (StationDomain) -> Unit,
    onDepartureChange: (LocalDateTime?) -> Unit,
    onArrivalChange: (LocalDateTime?) -> Unit,
    onCarriageNumberChange: (String) -> Unit,
    onCarriageClassTypeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(8.dp))

        SectionTitle("Номер поезда")
        FocusHighlightField(
            value = state.trainNumber,
            onValueChange = onTrainNumberChange,
            placeholder = "119A",
            imeAction = ImeAction.Next
        )

        Spacer(Modifier.height(8.dp))
        SectionHeader("Станции")

        Label("Станция отправления")
        DropDownMenu(
            modifier = Modifier.padding(horizontal = 16.dp),
            query = state.fromQuery,
            onQueryChange = onFromQueryChange,
            suggestions = fromSuggestions,
            onItemSelected = onSelectFrom,
            placeholderText = "Укажите станцию отправления"
        )

        Spacer(Modifier.height(8.dp))
        Label("Станция прибытия")
        DropDownMenu(
            modifier = Modifier.padding(horizontal = 16.dp),
            query = state.toQuery,
            onQueryChange = onToQueryChange,
            suggestions = toSuggestions,
            onItemSelected = onSelectTo,
            placeholderText = "Укажите станцию прибытия"
        )

        Spacer(Modifier.height(8.dp))
        SectionHeader("Время")

        TimeRow(
            arrival = state.arrival,
            onArrival = onArrivalChange,
            departure = state.departure,
            onDeparture = onDepartureChange
        )

        Spacer(Modifier.height(8.dp))
        SectionHeader("Вагон")

        Label("Номер вагона")
        FocusHighlightField(
            value = state.carriageNumber,
            onValueChange = onCarriageNumberChange,
            placeholder = ""
        )

        Spacer(Modifier.height(8.dp))
        Label("Класс обслуживания")
        FocusHighlightField(
            value = state.carriageClassType,
            onValueChange = onCarriageClassTypeChange,
            placeholder = "Не обязательно"
        )
    }
}

@Composable
private fun FocusHighlightField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    imeAction: ImeAction = ImeAction.Done,
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onFocusChangedState: (Boolean) -> Unit = {}
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
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
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
