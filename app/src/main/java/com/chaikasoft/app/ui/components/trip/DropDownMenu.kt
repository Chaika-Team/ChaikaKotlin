package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.StationDomain
import kotlin.math.max
import kotlinx.coroutines.flow.Flow

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
    itemTestTagFactory: (StationDomain) -> String = { station -> "stationItem_${station.code}" }
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val lazyItems = suggestionsFlow.collectAsLazyPagingItems()
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val menuState = mapMenuState(query = query, lazyItems = lazyItems)
    val resolvedPlaceholder = placeholderText.ifBlank {
        stringResource(R.string.dropdown_default_placeholder)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { newExpanded ->
            expanded = newExpanded
            if (newExpanded) {
                focusRequester.requestFocus()
                keyboard?.show()
            }
        },
        modifier = modifier.fillMaxWidth()
    ) {
        DropDownSearchField(
            value = query,
            query = query,
            onQueryChange = onQueryChange,
            placeholderText = resolvedPlaceholder,
            cornerRadius = cornerRadius,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            focusRequester = focusRequester,
            onShowKeyboard = { keyboard?.show() },
            fieldModifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .then(if (inputTestTag != null) Modifier.testTag(inputTestTag) else Modifier)
                .focusRequester(focusRequester)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            DropDownContent(
                state = menuState,
                lazyItems = lazyItems,
                onQueryChange = onQueryChange,
                onItemSelected = onItemSelected,
                itemTestTagFactory = itemTestTagFactory,
                onClose = { expanded = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownSearchField(
    value: String,
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String,
    cornerRadius: Dp,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    onShowKeyboard: () -> Unit,
    fieldModifier: Modifier
) {
    TextField(
        value = value,
        onValueChange = { newQuery ->
            if (newQuery != query) {
                onQueryChange(newQuery)
                if (newQuery.length >= 2) {
                    onExpandedChange(true)
                    focusRequester.requestFocus()
                    onShowKeyboard()
                }
            }
        },
        modifier = fieldModifier,
        singleLine = true,
        placeholder = { Text(placeholderText) },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        shape = RoundedCornerShape(cornerRadius),
        colors = ExposedDropdownMenuDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun DropDownContent(
    state: MenuUiState,
    lazyItems: LazyPagingItems<StationDomain>,
    onQueryChange: (String) -> Unit,
    onItemSelected: (StationDomain) -> Unit,
    itemTestTagFactory: (StationDomain) -> String,
    onClose: () -> Unit
) {
    when (state) {
        MenuUiState.NeedMoreCharacters ->
            StatusMenuItem(stringResource(R.string.dropdown_min_chars_hint))

        MenuUiState.InitialLoading ->
            StatusMenuItem(stringResource(R.string.dropdown_searching))

        is MenuUiState.RefreshError ->
            StatusMenuItem(state.message ?: stringResource(R.string.dropdown_error_loading))

        MenuUiState.Empty ->
            StatusMenuItem(stringResource(R.string.dropdown_nothing_found))

        is MenuUiState.Content -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                lazyItems.itemSnapshotList.items.forEach { station ->
                    DropdownMenuItem(
                        modifier = Modifier.testTag(itemTestTagFactory(station)),
                        text = { Text(station.name) },
                        onClick = {
                            onQueryChange(station.name)
                            onItemSelected(station)
                            onClose()
                        }
                    )
                }
                if (state.isAppending) {
                    StatusMenuItem(stringResource(R.string.dropdown_loading_more))
                }
            }
        }
    }
}

@Composable
private fun StatusMenuItem(text: String) {
    DropdownMenuItem(text = { Text(text) }, onClick = {})
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
