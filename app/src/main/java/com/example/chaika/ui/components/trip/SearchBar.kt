package com.example.chaika.ui.components.trip

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chaika.ui.theme.TripDimens

private const val TAG = "SEARCH_TRIP_INPUT_FIELD"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTripBar(
    modifier: Modifier = Modifier,
    initialQuery: String = "",
    onQueryChange: (String) -> Unit,
    placeholderText: String = "Поиск...",
    cornerRadius: Dp = 10.dp,
) {
    var query by rememberSaveable { mutableStateOf(initialQuery) }
    var active by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val colorScheme = MaterialTheme.colorScheme

    SearchBar(
        query = query,
        onQueryChange = { newQuery ->
            query = newQuery
            onQueryChange(newQuery)
        },
        onSearch = {
            focusManager.clearFocus()
            active = false
        },
        active = active,
        onActiveChange = { active = it },
        modifier =
            modifier
                .fillMaxWidth()
                .height(TripDimens.SearchBarHeight)
                .semantics(mergeDescendants = true) {
                    this.testTag = TAG
                },
        placeholder = {
            Text(
                text = placeholderText,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 16.sp,
            )
        },
        shape = RoundedCornerShape(cornerRadius),
        colors =
            SearchBarDefaults.colors(
                containerColor = colorScheme.surfaceVariant,
                inputFieldColors =
                    TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
            ),
    ) {}
}

@Preview
@Composable
fun SearchTripBarPreview() {
    SearchTripBar(
        onQueryChange = {},
        placeholderText = "Сегодня, 31 января 2025",
    )
}
