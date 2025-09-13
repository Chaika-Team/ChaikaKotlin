package com.chaikasoft.app.ui.components.trip

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.chaikasoft.app.domain.models.trip.StationDomain
import kotlinx.coroutines.flow.Flow
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    suggestionsFlow: Flow<PagingData<StationDomain>>,
    onItemSelected: (StationDomain) -> Unit,
    placeholderText: String = "Выберите...",
    cornerRadius: Dp = 10.dp
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val lazyItems = suggestionsFlow.collectAsLazyPagingItems()

    // NEW: будем целенаправленно фокусить поле и открывать клавиатуру
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // координаты якоря (оставляем как у тебя)
    var anchorRect by remember { mutableStateOf(IntRect(0, 0, 0, 0)) }
    var anchorWidthPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { newExpanded ->
            expanded = newExpanded
            if (newExpanded) {
                // при открытии меню сразу переводим фокус в поле + показываем ИМЕ
                focusRequester.requestFocus()
                keyboard?.show()
            }
        },
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = query,
            onValueChange = { newQuery ->
                if (newQuery != query) {
                    onQueryChange(newQuery)
                    if (newQuery.length >= 2) {
                        expanded = true
                        focusRequester.requestFocus()
                        keyboard?.show()
                    }
                }
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onGloballyPositioned { coords ->
                    val b = coords.boundsInWindow()
                    anchorRect = IntRect(b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt())
                    anchorWidthPx = coords.size.width
                },
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

    if (expanded) {
        Popup(
            properties = PopupProperties(
                focusable = false,
                clippingEnabled = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = { expanded = false },
            popupPositionProvider = remember(anchorRect) {
                object : PopupPositionProvider {
                    override fun calculatePosition(
                        anchorBounds: IntRect,
                        windowSize: IntSize,
                        layoutDirection: LayoutDirection,
                        popupContentSize: IntSize
                    ): IntOffset {
                        val x = anchorRect.left
                        val y = anchorRect.bottom
                        return IntOffset(
                            x.coerceIn(0, max(0, windowSize.width - popupContentSize.width)),
                            y.coerceIn(0, max(0, windowSize.height - popupContentSize.height))
                        )
                    }
                }
            }
        ) {
            Surface(
                shape = RoundedCornerShape(cornerRadius),
                tonalElevation = 4.dp,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .width(with(density) { anchorWidthPx.toDp() })
                    .heightIn(max = 320.dp)           // фикс. высота → не переворачиваемся
            ) {
                // Рендер списка + состояния загрузки
                val isInitialLoading = lazyItems.loadState.refresh is LoadState.Loading && lazyItems.itemCount == 0
                val isAppending = lazyItems.loadState.append is LoadState.Loading

                when {
                    query.length < 2 -> DropdownMenuItem(text = { Text("Введите минимум 2 символа") }, onClick = {})
                    isInitialLoading -> DropdownMenuItem(text = { Text("Поиск...") }, onClick = {})
                    lazyItems.loadState.refresh is LoadState.Error -> {
                        val msg = (lazyItems.loadState.refresh as LoadState.Error).error.localizedMessage ?: "Ошибка загрузки"
                        DropdownMenuItem(text = { Text(msg) }, onClick = {})
                    }
                    lazyItems.itemCount == 0 -> DropdownMenuItem(text = { Text("Ничего не найдено") }, onClick = {})
                    else -> {
                        LazyColumn {
                            items(lazyItems.itemCount) { i ->
                                lazyItems[i]?.let { station ->
                                    DropdownMenuItem(
                                        text = { Text(station.name) },
                                        onClick = {
                                            onQueryChange(station.name)
                                            onItemSelected(station)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                            if (isAppending) {
                                item { DropdownMenuItem(text = { Text("Загружаем ещё...") }, onClick = {}) }
                            }
                        }
                    }
                }
            }
        }
    }
}

