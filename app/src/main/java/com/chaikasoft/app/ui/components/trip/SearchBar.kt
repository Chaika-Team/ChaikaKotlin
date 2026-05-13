package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaikasoft.app.ui.theme.TripDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTripBar(
    modifier: Modifier = Modifier,
    value: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String = "",
    cornerRadius: Dp = 10.dp,
    readOnly: Boolean = false,
    testTag: String? = null,
    onClick: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TripDimens.SearchBarHeight)
    ) {
        if (readOnly && onClick != null) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
                    .clickable {
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                        onClick()
                    },
                shape = RoundedCornerShape(cornerRadius),
                color = colorScheme.surfaceVariant
            ) {
                val hasValue = value.isNotBlank()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (hasValue) value else placeholderText,
                        color = if (hasValue) {
                            colorScheme.onSurface
                        } else {
                            colorScheme.onSurfaceVariant.copy(
                                alpha = 0.6f
                            )
                        },
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            SearchBar(
                query = value,
                onQueryChange = { newQuery -> onQueryChange(newQuery) },
                onSearch = { focusManager.clearFocus() },
                active = false,
                onActiveChange = {},
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
                placeholder = {
                    Text(
                        text = placeholderText,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 16.sp
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
    }
}

@Preview
@Composable
fun SearchTripBarPreview() {
    SearchTripBar(
        modifier = Modifier,
        value = "",
        onQueryChange = {},
        placeholderText = "2026-03-27",
        cornerRadius = 10.dp
    )
}
