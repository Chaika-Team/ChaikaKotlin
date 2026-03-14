package com.chaikasoft.app.ui.viewModels.delegates

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.usecases.GetPagedStationSuggestionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class StationSuggestionsDelegate(
    private val getPagedSuggestions: GetPagedStationSuggestionsUseCase,
    scope: CoroutineScope
) {

    private val _fromQuery = MutableStateFlow("")
    private val _toQuery = MutableStateFlow("")

    val fromQuery: StateFlow<String> = _fromQuery.asStateFlow()
    val toQuery: StateFlow<String> = _toQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val fromSuggestions: Flow<PagingData<StationDomain>> =
        _fromQuery
            .map { it.trim() }
            .debounce(DEBOUNCE_MS)
            .distinctUntilChanged()
            .flatMapLatest { q ->
                if (q.length < MIN_QUERY_LEN) flowOf(PagingData.empty())
                else getPagedSuggestions(q, pageSize = PAGE_SIZE)
            }
            .cachedIn(scope)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val toSuggestions: Flow<PagingData<StationDomain>> =
        _toQuery
            .map { it.trim() }
            .debounce(DEBOUNCE_MS)
            .distinctUntilChanged()
            .flatMapLatest { q ->
                if (q.length < MIN_QUERY_LEN) flowOf(PagingData.empty())
                else getPagedSuggestions(q, pageSize = PAGE_SIZE)
            }
            .cachedIn(scope)

    fun onFromQueryChanged(text: String) { _fromQuery.value = text }
    fun onToQueryChanged(text: String)   { _toQuery.value = text }

    fun reset() {
        _fromQuery.value = ""
        _toQuery.value = ""
    }

    private companion object {
        const val DEBOUNCE_MS = 500L
        const val MIN_QUERY_LEN = 2
        const val PAGE_SIZE = 20
    }
}