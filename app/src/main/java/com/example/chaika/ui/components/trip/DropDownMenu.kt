package com.example.chaika.ui.components.trip

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.ui.theme.TripDimens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    modifier: Modifier = Modifier,
    initialQuery: String = "",
    onQueryChange: (String) -> Unit,
    onItemSelected: (StationDomain) -> Unit,
    placeholderText: String = "Выберите...",
    cornerRadius: Dp = 10.dp,
    suggestStations: suspend (String, Int) -> List<StationDomain>,
    isStationSearch: Boolean = false,
    maxSuggestions: Int = 10
) {
    var query by rememberSaveable { mutableStateOf(initialQuery) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var suggestions by rememberSaveable { mutableStateOf<List<StationDomain>>(emptyList()) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val colorScheme = MaterialTheme.colorScheme
    val searchQuery = remember { MutableStateFlow("") }

    // Обновляем searchQuery при изменении query (с debounce)
    LaunchedEffect(query) {
        if (isStationSearch) {
            searchQuery.update { query }
        }
    }

    LaunchedEffect(searchQuery) {
        searchQuery
            .debounce(300)
            .collectLatest { currentQuery ->
                if (currentQuery.length >= 2) {
                    try {
                        isLoading = true
                        error = null
                        suggestions = withContext(Dispatchers.IO) {
                            suggestStations(currentQuery, maxSuggestions)
                        }
                    } catch (e: Exception) {
                        error = e.localizedMessage
                        suggestions = emptyList()
                    } finally {
                        isLoading = false
                    }
                } else {
                    suggestions = emptyList()
                }
            }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .fillMaxWidth()
            .height(TripDimens.SearchBarHeight)
    ) {
        TextField(
            value = query,
            onValueChange = { newQuery ->
                query = newQuery
                onQueryChange(newQuery)
                if (isStationSearch) {
                    expanded = newQuery.isNotEmpty()
                }
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholderText,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            },
            readOnly = false,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(cornerRadius),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        if (expanded) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                when {
                    // Загрузка
                    isLoading -> {
                        DropdownMenuItem(
                            text = { Text("Поиск...") },
                            onClick = {}
                        )
                    }
                    // Ошибка
                    error != null -> {
                        DropdownMenuItem(
                            text = { Text(error!!) },
                            onClick = {}
                        )
                    }
                    // Поиск станций (минимум 2 символа)
                    isStationSearch && query.length < 2 -> {
                        DropdownMenuItem(
                            text = { Text("Введите минимум 2 символа") },
                            onClick = {}
                        )
                    }
                    // Нет результатов
                    isStationSearch && suggestions.isEmpty() && query.isNotEmpty() -> {
                        DropdownMenuItem(
                            text = { Text("Ничего не найдено") },
                            onClick = {}
                        )
                    }
                    // Показать результаты
                    suggestions.isNotEmpty() -> {
                        suggestions.forEach { station ->
                            DropdownMenuItem(
                                text = { Text(station.name) },
                                onClick = {
                                    query = station.name
                                    onItemSelected(station)
                                    expanded = false
                                }
                            )
                        }
                    }
                    // Обычный выпадающий список (если не поиск)
                    !isStationSearch -> {
                        // Здесь можно добавить статические варианты, если нужно
                        DropdownMenuItem(
                            text = { Text("Нет доступных вариантов") },
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DropdownTripMenuPreview() {
    DropDownMenu(
        onQueryChange = {},
        onItemSelected = {},
        suggestStations = { _, _ -> emptyList() }
    )
}