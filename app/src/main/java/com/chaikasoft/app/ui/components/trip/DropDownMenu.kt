package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.StationDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    suggestionsFlow: Flow<PagingData<StationDomain>>,
    onItemSelected: (StationDomain) -> Unit,
    placeholderText: String = "",
    cornerRadius: Dp = 10.dp,
    inputTestTag: String? = null,
    menuTestTag: String? = null,
    itemTestTagFactory: ((StationDomain) -> String)? = null
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val lazyItems = suggestionsFlow.collectAsLazyPagingItems()
    val resolvedPlaceholder = placeholderText.ifBlank {
        stringResource(R.string.dropdown_default_placeholder)
    }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    StationPickerAnchor(
        value = query,
        placeholderText = resolvedPlaceholder,
        cornerRadius = cornerRadius,
        onClick = {
            focusManager.clearFocus(force = true)
            keyboard?.hide()
            showPicker = true
        },
        modifier = modifier
            .fillMaxWidth()
            .then(if (inputTestTag != null) Modifier.testTag(inputTestTag) else Modifier)
    )

    if (showPicker) {
        StationPickerSheet(
            query = query,
            onQueryChange = onQueryChange,
            lazyItems = lazyItems,
            placeholderText = resolvedPlaceholder,
            menuTestTag = menuTestTag,
            itemTestTagFactory = itemTestTagFactory,
            onDismiss = {
                keyboard?.hide()
                focusManager.clearFocus(force = true)
                showPicker = false
            },
            onItemSelected = { station ->
                onQueryChange(station.name)
                onItemSelected(station)
                keyboard?.hide()
                focusManager.clearFocus(force = true)
                showPicker = false
            }
        )
    }
}

@Composable
private fun StationPickerAnchor(
    value: String,
    placeholderText: String,
    cornerRadius: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val hasValue = value.isNotBlank()

    Surface(
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(cornerRadius),
        color = colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (hasValue) value else placeholderText,
                modifier = Modifier.weight(1f),
                color = if (hasValue) {
                    colorScheme.onSurface
                } else {
                    colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationPickerSheet(
    query: String,
    onQueryChange: (String) -> Unit,
    lazyItems: LazyPagingItems<StationDomain>,
    placeholderText: String,
    menuTestTag: String?,
    itemTestTagFactory: ((StationDomain) -> String)?,
    onDismiss: () -> Unit,
    onItemSelected: (StationDomain) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        StationPickerSheetContent(
            query = query,
            onQueryChange = onQueryChange,
            lazyItems = lazyItems,
            placeholderText = placeholderText,
            menuTestTag = menuTestTag,
            itemTestTagFactory = itemTestTagFactory,
            onItemSelected = { station ->
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    onItemSelected(station)
                }
            }
        )
    }
}

@Composable
private fun StationPickerSheetContent(
    query: String,
    onQueryChange: (String) -> Unit,
    lazyItems: LazyPagingItems<StationDomain>,
    placeholderText: String,
    menuTestTag: String?,
    itemTestTagFactory: ((StationDomain) -> String)?,
    onItemSelected: (StationDomain) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val menuState = mapMenuState(query = query, lazyItems = lazyItems)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (menuTestTag != null) Modifier.testTag(menuTestTag) else Modifier)
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = placeholderText,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(14.dp))
        StationSearchField(
            value = query,
            onValueChange = onQueryChange,
            placeholderText = placeholderText,
            focusRequester = focusRequester,
            onDone = { keyboard?.hide() }
        )
        Spacer(Modifier.height(12.dp))
        StationPickerContent(
            state = menuState,
            lazyItems = lazyItems,
            itemTestTagFactory = itemTestTagFactory,
            onItemSelected = onItemSelected
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun StationSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    focusRequester: FocusRequester,
    onDone: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        placeholder = { Text(placeholderText) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (value.isNotBlank()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onDone() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colorScheme.surfaceContainerHighest,
            unfocusedContainerColor = colorScheme.surfaceContainerHighest,
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = colorScheme.primary
        )
    )
}

@Composable
private fun StationPickerContent(
    state: MenuUiState,
    lazyItems: LazyPagingItems<StationDomain>,
    itemTestTagFactory: ((StationDomain) -> String)?,
    onItemSelected: (StationDomain) -> Unit
) {
    when (state) {
        MenuUiState.NeedMoreCharacters ->
            StatusRow(stringResource(R.string.dropdown_min_chars_hint))

        MenuUiState.InitialLoading ->
            StatusRow(stringResource(R.string.dropdown_searching))

        is MenuUiState.RefreshError ->
            StatusRow(state.message ?: stringResource(R.string.dropdown_error_loading))

        MenuUiState.Empty ->
            StatusRow(stringResource(R.string.dropdown_nothing_found))

        is MenuUiState.Content -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                items(lazyItems.itemCount) { index ->
                    val station = lazyItems[index]
                    if (station != null) {
                        StationRow(
                            station = station,
                            modifier = if (itemTestTagFactory != null) {
                                Modifier.testTag(itemTestTagFactory(station))
                            } else {
                                Modifier
                            },
                            onClick = { onItemSelected(station) }
                        )
                    }
                }
                if (state.isAppending) {
                    item {
                        StatusRow(stringResource(R.string.dropdown_loading_more))
                    }
                }
            }
        }
    }
}

@Composable
private fun StationRow(station: StationDomain, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Place,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .weight(1f)
            ) {
                Text(
                    text = station.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (station.code.isNotBlank()) {
                    Text(
                        text = station.code,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private sealed interface MenuUiState {
    data object NeedMoreCharacters : MenuUiState
    data object InitialLoading : MenuUiState
    data class RefreshError(val message: String?) : MenuUiState
    data object Empty : MenuUiState
    data class Content(val isAppending: Boolean) : MenuUiState
}

private fun mapMenuState(query: String, lazyItems: LazyPagingItems<StationDomain>): MenuUiState {
    if (query.length < 2) return MenuUiState.NeedMoreCharacters

    val refreshState = lazyItems.loadState.refresh
    val isInitialLoading = refreshState is LoadState.Loading && lazyItems.itemCount == 0
    if (isInitialLoading) return MenuUiState.InitialLoading

    if (refreshState is LoadState.Error) {
        return MenuUiState.RefreshError(refreshState.error.localizedMessage)
    }

    if (lazyItems.itemCount == 0) return MenuUiState.Empty

    val isAppending = lazyItems.loadState.append is LoadState.Loading
    return MenuUiState.Content(isAppending = isAppending)
}
