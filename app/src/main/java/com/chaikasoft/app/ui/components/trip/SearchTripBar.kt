package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaikasoft.app.ui.theme.TripDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchTripBar(
    value: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    cornerRadius: Dp = 10.dp,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    testTag: String? = null
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = TripDimens.SearchBarHeight)
    ) {
        if (readOnly && onClick != null) {
            ReadOnlySearchTripBar(
                value = value,
                placeholderText = placeholderText,
                cornerRadius = cornerRadius,
                testTag = testTag,
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus(force = true)
                    onClick()
                }
            )
        } else {
            EditableSearchTripBar(
                value = value,
                placeholderText = placeholderText,
                cornerRadius = cornerRadius,
                testTag = testTag,
                onQueryChange = onQueryChange,
                onSearch = { focusManager.clearFocus() }
            )
        }
    }
}

@Composable
private fun ReadOnlySearchTripBar(
    value: String,
    placeholderText: String,
    cornerRadius: Dp,
    testTag: String?,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .testTagIfPresent(testTag)
            .fillMaxWidth()
            .defaultMinSize(minHeight = TripDimens.SearchBarHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(cornerRadius),
        color = colorScheme.surfaceVariant
    ) {
        SearchTripBarText(
            text = value.ifBlank { placeholderText },
            color = if (value.isBlank()) {
                colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            } else {
                colorScheme.onSurface
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableSearchTripBar(
    value: String,
    placeholderText: String,
    cornerRadius: Dp,
    testTag: String?,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    SearchBar(
        query = value,
        onQueryChange = onQueryChange,
        onSearch = { onSearch() },
        active = false,
        onActiveChange = {},
        modifier = Modifier
            .testTagIfPresent(testTag)
            .fillMaxWidth()
            .defaultMinSize(minHeight = TripDimens.SearchBarHeight),
        placeholder = {
            Text(
                text = placeholderText,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        shape = RoundedCornerShape(cornerRadius),
        colors = SearchBarDefaults.colors(
            containerColor = colorScheme.surfaceVariant,
            inputFieldColors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    ) { }
}

@Composable
private fun SearchTripBarText(text: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = TripDimens.SearchBarHeight)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun Modifier.testTagIfPresent(testTag: String?): Modifier =
    then(if (testTag != null) Modifier.testTag(testTag) else Modifier)

@Preview
@Composable
private fun SearchTripBarPreview() {
    SearchTripBar(
        modifier = Modifier,
        value = "",
        onQueryChange = {},
        placeholderText = "2026-03-27",
        cornerRadius = 10.dp
    )
}
